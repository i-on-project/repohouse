package com.isel.leic.ps.ionClassCode.http.controllers.web

import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Teacher
import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.domain.input.OtpInputModel
import com.isel.leic.ps.ionClassCode.domain.input.StudentInput
import com.isel.leic.ps.ionClassCode.domain.input.TeacherInput
import com.isel.leic.ps.ionClassCode.http.GITHUB_BASE_URL
import com.isel.leic.ps.ionClassCode.http.GITHUB_OAUTH_URI
import com.isel.leic.ps.ionClassCode.http.Status
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.input.SchoolIdInputModel
import com.isel.leic.ps.ionClassCode.http.model.output.AuthRedirect
import com.isel.leic.ps.ionClassCode.http.model.output.AuthStateOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.OAuthState
import com.isel.leic.ps.ionClassCode.http.model.output.RegisterOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.StatusOutputModel
import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.SirenModel
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.GithubServices
import com.isel.leic.ps.ionClassCode.services.OutboxServices
import com.isel.leic.ps.ionClassCode.services.OutboxServicesError
import com.isel.leic.ps.ionClassCode.services.StudentServices
import com.isel.leic.ps.ionClassCode.services.TeacherServices
import com.isel.leic.ps.ionClassCode.services.UserServices
import com.isel.leic.ps.ionClassCode.utils.Result
import com.isel.leic.ps.ionClassCode.utils.cypher.AESDecrypt
import com.isel.leic.ps.ionClassCode.utils.cypher.AESEncrypt
import jakarta.servlet.http.HttpServletResponse
import okhttp3.internal.EMPTY_REQUEST
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

const val ORG_NAME = "test-project-isel"
const val GITHUB_TEACHER_SCOPE = "read:org user:email repo"
const val MOBILE_GITHUB_TEACHER_SCOPE = "admin:org%20user:email%20repo"
const val GITHUB_STUDENT_SCOPE = "repo user:email"

const val STUDENT_COOKIE_NAME = "Student"
const val TEACHER_COOKIE_NAME = "Teacher"
const val STATE_COOKIE_NAME = "userState"
const val POSITION_COOKIE_NAME = "position"
const val GITHUB_ID_COOKIE_NAME = "userGithubId"
const val STATE_COOKIE_PATH = Uris.CALLBACK_PATH
const val HALF_HOUR: Long = 60 * 30
const val FULL_DAY: Long = 60 * 60 * 24
const val AUTHORIZATION_COOKIE_NAME = "Session"

const val TEST = false
val URI = if (TEST) "http://localhost:3000" else System.getenv("NGROK_URI") ?: "http://localhost:3000"

/**
 * This controller is responsible for the authentication of the users.
 * It uses the OAuth2 protocol to authenticate the users.
 * It also handles the callback from the OAuth2 provider.
 */
@RestController
class AuthController(
    private val userServices: UserServices,
    private val studentServices: StudentServices,
    private val teacherServices: TeacherServices,
    private val outboxServices: OutboxServices,
    private val githubServices: GithubServices,
) {

    /**
     * Teacher authentication with the respective scope.
     */
    @GetMapping(Uris.AUTH_TEACHER_PATH, produces = ["application/vnd.siren+json"])
    fun authTeacher(
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        val state = generateUserState()
        response.addHeader(HttpHeaders.SET_COOKIE, state.cookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, generateUserPosition(TEACHER_COOKIE_NAME).toString())
        return siren(AuthRedirect(url = "$GITHUB_BASE_URL${GITHUB_OAUTH_URI(GITHUB_TEACHER_SCOPE, state.value)}")) {
            clazz("auth")
            link(rel = LinkRelation("self"), href = Uris.AUTH_TEACHER_PATH)
        }
    }

    /**
     * Student authentication with the respective scope.
     */
    @GetMapping(Uris.AUTH_STUDENT_PATH, produces = ["application/vnd.siren+json"])
    fun authStudent(
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        val state = generateUserState()
        response.addHeader(HttpHeaders.SET_COOKIE, state.cookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, generateUserPosition(STUDENT_COOKIE_NAME).toString())
        return siren(AuthRedirect(url = "$GITHUB_BASE_URL${GITHUB_OAUTH_URI(GITHUB_STUDENT_SCOPE, state.value)}")) {
            clazz("auth")
            link(rel = LinkRelation("self"), href = Uris.AUTH_STUDENT_PATH)
        }
    }

    /**
     * Checks if user is authenticated.
     */
    @GetMapping(Uris.AUTH_STATE_PATH, produces = ["application/vnd.siren+json"])
    fun authState(
        user: User,
    ): ResponseEntity<SirenModel<AuthStateOutputModel>> {
        return siren(
            AuthStateOutputModel(
                if (user is Student) STUDENT_COOKIE_NAME else TEACHER_COOKIE_NAME,
                true,
                user.githubId,
                user.id,
            ),
        ) {
            clazz("auth")
            link(rel = LinkRelation("self"), href = Uris.AUTH_STATE_PATH)
        }
    }

    /**
     * Callback from the OAuth2 provider.
     * It fetches the access token and the user info.
     * Check if the user is created and verified, and computes accordingly.
     */
    @GetMapping(Uris.CALLBACK_PATH)
    suspend fun callback(
        @RequestParam code: String,
        @RequestParam state: String,
        @CookieValue position: String,
        @CookieValue userState: String,
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        if (state != userState) {
            return ResponseEntity
                .status(Status.REDIRECT)
                .header(HttpHeaders.LOCATION, "$URI/auth/error/callback")
                .body(EMPTY_REQUEST)
        }
        val accessToken = githubServices.fetchAccessToken(code)
        val userGithubInfo = githubServices.fetchUserInfo(accessToken.access_token)
        return when (val userInfo = userServices.getUserByGithubId(userGithubInfo.id)) {
            is Result.Success -> {
                if (userInfo.value.isCreated) {
                    when {
                        userInfo.value is Student && position == STUDENT_COOKIE_NAME -> {
                            val cookie = generateSessionCookie(userInfo.value.token)
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .header(HttpHeaders.LOCATION, "$URI/menu/callback/student?githubId=${userInfo.value.githubId}&userId=${userInfo.value.id}")
                                .body(EMPTY_REQUEST)
                        }
                        userInfo.value is Teacher && position == TEACHER_COOKIE_NAME -> {
                            when (teacherServices.updateTeacherGithubToken(userInfo.value.id, accessToken.access_token)) {
                                is Result.Problem ->
                                    ResponseEntity
                                        .status(Status.REDIRECT)
                                        .header(HttpHeaders.LOCATION, "$URI/auth/error/callback")
                                        .body(EMPTY_REQUEST)
                                is Result.Success -> {
                                    val cookie = generateSessionCookie(userInfo.value.token)
                                    ResponseEntity
                                        .status(Status.REDIRECT)
                                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                        .header(HttpHeaders.LOCATION, "$URI/menu/callback/teacher?githubId=${userInfo.value.githubId}&userId=${userInfo.value.id}")
                                        .body(EMPTY_REQUEST)
                                }
                            }
                        }
                        else -> {
                            val authorizationCookie = deleteSessionCookie()
                            val githubIdCookie = deleteGithubIdCookie()
                            response.setHeader(HttpHeaders.SET_COOKIE, authorizationCookie.toString())
                            response.setHeader(HttpHeaders.SET_COOKIE, githubIdCookie.toString())
                            return ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.LOCATION, "$URI/auth/fail/callback")
                                .body(EMPTY_REQUEST)
                        }
                    }
                } else {
                    if (position == TEACHER_COOKIE_NAME) {
                        val cookie = generateGithubIdCookie(userGithubInfo.id)
                        ResponseEntity
                            .status(Status.REDIRECT)
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .header(HttpHeaders.LOCATION, "$URI/auth/status")
                            .body(EMPTY_REQUEST)
                    } else {
                        val cookie = generateGithubIdCookie(userGithubInfo.id)
                        ResponseEntity
                            .status(Status.REDIRECT)
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .header(HttpHeaders.LOCATION, "$URI/auth/verify")
                            .body(EMPTY_REQUEST)
                    }
                }
            }
            is Result.Problem -> {
                val userEmail = githubServices.fetchUserEmails(accessToken.access_token).first { it.primary }
                if (position == TEACHER_COOKIE_NAME) {
                    when (
                        teacherServices.createPendingTeacher(
                            TeacherInput(
                                email = userEmail.email,
                                githubUsername = userGithubInfo.login,
                                githubId = userGithubInfo.id,
                                token = generateRandomToken(),
                                name = userGithubInfo.name ?: userGithubInfo.login,
                                githubToken = accessToken.access_token,
                            ),
                        )
                    ) {
                        is Result.Success -> {
                            val cookie = generateGithubIdCookie(userGithubInfo.id)
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .header(HttpHeaders.LOCATION, "$URI/auth/create/callback/teacher")
                                .body(EMPTY_REQUEST)
                        }
                        is Result.Problem ->
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.LOCATION, "$URI/auth/error/callback")
                                .body(EMPTY_REQUEST)
                    }
                } else {
                    when (
                        studentServices.createPendingStudent(
                            StudentInput(
                                email = userEmail.email,
                                githubUsername = userGithubInfo.login,
                                githubId = userGithubInfo.id,
                                token = generateRandomToken(),
                                name = userGithubInfo.name ?: userGithubInfo.login,
                            ),
                        )
                    ) {
                        is Result.Success -> {
                            val cookie = generateGithubIdCookie(userGithubInfo.id)
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .header(HttpHeaders.LOCATION, "$URI/auth/create/callback/student")
                                .body(EMPTY_REQUEST)
                        }
                        is Result.Problem ->
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.LOCATION, "$URI/auth/error/callback")
                                .body(EMPTY_REQUEST)
                    }
                }
            }
        }
    }

    /**
     * Get the register info of a user.
     */
    @GetMapping(Uris.AUTH_REGISTER_PATH)
    fun getRegisterInfo(
        @CookieValue userGithubId: String,
        @CookieValue position: String,
    ): ResponseEntity<*> {
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val userInfo = userServices.getPendingUserByGithubId(githubId, position)) {
            is Result.Success -> siren(RegisterOutputModel(userInfo.value.name, userInfo.value.email, userInfo.value.githubUsername)) {
                clazz("registerInfo")
                link(rel = LinkRelation("self"), href = Uris.AUTH_REGISTER_PATH)
            }
            is Result.Problem -> userServices.problem(userInfo.value)
        }
    }

    /**
     * Register a teacher.
     */
    @PostMapping(Uris.AUTH_REGISTER_TEACHER_PATH)
    fun registerTeacher(
        @CookieValue userGithubId: String,
        @CookieValue position: String,
    ): ResponseEntity<*> {
        if (position != TEACHER_COOKIE_NAME) return Problem.badRequest
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val teacher = teacherServices.confirmPendingTeacher(githubId)) {
            is Result.Success -> siren(StatusOutputModel("Teacher Request Register", "Need to wait for other Teacher to confirm your request.")) {
                clazz("registerTeacher")
                action(title = "registerTeacher", href = Uris.AUTH_REGISTER_TEACHER_PATH, method = HttpMethod.POST, type = "application/json", block = {})
            }
            is Result.Problem -> teacherServices.problem(teacher.value)
        }
    }

    /**
     * Register a student with a school id.
     */
    @PostMapping(Uris.AUTH_REGISTER_STUDENT_PATH)
    fun createStudent(
        @CookieValue userGithubId: String,
        @CookieValue position: String,
        @RequestBody input: SchoolIdInputModel,
    ): ResponseEntity<*> {
        if (position != STUDENT_COOKIE_NAME) return Problem.badRequest
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val student = studentServices.createStudent(githubId, input.schoolId)) {
            is Result.Success -> {
                when (val userOutbox = outboxServices.createUserVerification(student.value.id)) {
                    is Result.Success -> siren(StatusOutputModel("Verify user", "Verify your email to proceed with the verification")) {
                        clazz("registerStudent")
                        action(title = "registerStudent", href = Uris.AUTH_REGISTER_STUDENT_PATH, method = HttpMethod.POST, type = "application/json", block = {
                            numberField("schoolId")
                        })
                    }
                    is Result.Problem -> when (userOutbox.value) {
                        is OutboxServicesError.CooldownNotExpired -> siren(
                            StatusOutputModel(
                                "On cooldown",
                                "You are on cooldown, try again in ${userOutbox.value.cooldown} seconds",
                            ),
                        ) {
                            clazz("registerStudent")
                            action(title = "registerStudent", href = Uris.AUTH_REGISTER_STUDENT_PATH, method = HttpMethod.POST, type = "application/json", block = {
                                numberField("schoolId")
                            })
                        }
                        else -> outboxServices.problem(userOutbox.value)
                    }
                }
            }
            is Result.Problem -> studentServices.problem(student.value)
        }
    }

    /**
     * Get the status of the user registration.
     */
    @GetMapping(Uris.AUTH_STATUS_PATH)
    fun getStatus(
        @CookieValue position: String,
        @CookieValue userGithubId: String,
    ): ResponseEntity<*> {
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val userInfo = userServices.getPendingUserByGithubId(githubId, position)) {
            is Result.Success -> {
                val user = userServices.getUserByGithubId(githubId)
                if (userInfo.value.isCreated && user is Result.Success) {
                    return siren(
                        StatusOutputModel(
                            "You are now eligible to use the application.",
                            "Return to home to authenticate yourself.",
                        ),
                    ) {
                        clazz("status")
                        link(rel = LinkRelation("self"), href = Uris.AUTH_STATUS_PATH)
                    }
                } else if (userInfo.value.isCreated) {
                    return when (position) {
                        TEACHER_COOKIE_NAME -> siren(
                            StatusOutputModel(
                                "Needing approval.",
                                "Wait for approval from other teachers.",
                            ),
                        ) {
                            clazz("status")
                            link(rel = LinkRelation("self"), href = Uris.AUTH_STATUS_PATH)
                        }
                        else -> siren(
                            StatusOutputModel(
                                "Needing action.",
                                "Verify your email to proceed with the verification or register using your school id.",
                            ),
                        ) {
                            clazz("status")
                            link(rel = LinkRelation("self"), href = Uris.AUTH_STATUS_PATH)
                        }
                    }
                }
                return siren(
                    StatusOutputModel(
                        "Requiring Confirmation.",
                        "You must confirm your pending registration request.",
                    ),
                ) {
                    clazz("status")
                    link(rel = LinkRelation("self"), href = Uris.AUTH_STATUS_PATH)
                }
            }
            is Result.Problem -> userServices.problem(userInfo.value)
        }
    }

    /**
     * Verify a student, using the OTP sent.
     */
    @PostMapping(Uris.AUTH_REGISTER_VERIFICATION_PATH, produces = ["application/vnd.siren+json"])
    fun authRegisterVerifyStudent(
        @CookieValue userGithubId: String,
        @RequestBody input: OtpInputModel,
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val user = userServices.getUserByGithubId(githubId)) {
            is Result.Success -> when (val checkOTP = outboxServices.checkOtp(user.value.id, input.otp)) {
                is Result.Success -> {
                    siren(StatusOutputModel("User verified.", "User verified successfully, you can navigate to menu.")) {
                        clazz("verifyStudent")
                        action(title = "verify", href = Uris.AUTH_REGISTER_VERIFICATION_PATH, method = HttpMethod.POST, type = "application/json", block = {
                            numberField("otp")
                        })
                    }
                }
                is Result.Problem -> outboxServices.problem(checkOTP.value)
            }
            is Result.Problem -> userServices.problem(user.value)
        }
    }

    /**
     * Resend email verification.
     */
    @PostMapping(Uris.AUTH_RESEND_EMAIL_PATH, produces = ["application/vnd.siren+json"])
    fun authResendEmailPath(
        @CookieValue userGithubId: String,
    ): ResponseEntity<*> {
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val user = userServices.getUserByGithubId(githubId)) {
            is Result.Success -> when (val resendEmail = outboxServices.resendEmail(user.value.id)) {
                is Result.Success -> {
                    siren(StatusOutputModel("Email sent.", "Email sent successfully, check your email.")) {
                        clazz("resendEmail")
                        action(title = "self", href = Uris.AUTH_RESEND_EMAIL_PATH, method = HttpMethod.POST, type = "application/json", block = {})
                    }
                }
                is Result.Problem -> outboxServices.problem(resendEmail.value)
            }
            is Result.Problem -> userServices.problem(user.value)
        }
    }

    /**
     * Logout the user.
     */
    @PostMapping(Uris.LOGOUT)
    fun logout(
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        response.addHeader(HttpHeaders.SET_COOKIE, deleteSessionCookie().toString())
        response.addHeader(HttpHeaders.SET_COOKIE, deleteGithubIdCookie().toString())
        return siren(null) {
            clazz("logout")
            action(title = "logout", href = Uris.LOGOUT, method = HttpMethod.POST, type = "application/json", block = {})
        }
    }

    /**
     * Create a user state cookie.
     */
    private fun generateUserState(): OAuthState {
        val state = UUID.randomUUID().toString()
        val cookie = ResponseCookie.from(STATE_COOKIE_NAME, state)
            .path(STATE_COOKIE_PATH)
            .path("/api")
            .maxAge(HALF_HOUR)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .build()

        return OAuthState(state, cookie)
    }

    /**
     * Create a session cookie.
     */
    private fun generateSessionCookie(token: String): ResponseCookie {
        return ResponseCookie.from(AUTHORIZATION_COOKIE_NAME, AESEncrypt.encrypt(token))
            .path("/api")
            .maxAge(FULL_DAY)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .build()
    }

    /**
     * Create a user position cookie.
     */
    private fun generateUserPosition(position: String): ResponseCookie {
        return ResponseCookie.from(POSITION_COOKIE_NAME, position)
            .path("/api")
            .maxAge(HALF_HOUR)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .build()
    }

    /**
     * Create a user GitHub id cookie.
     */
    private fun generateGithubIdCookie(gitHubId: Long): ResponseCookie {
        return ResponseCookie.from(GITHUB_ID_COOKIE_NAME, AESEncrypt.encrypt(gitHubId.toString()))
            .path("/api")
            .maxAge(HALF_HOUR)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .build()
    }

    /**
     * Delete a session cookie.
     */
    private fun deleteSessionCookie(): ResponseCookie {
        return ResponseCookie.from(AUTHORIZATION_COOKIE_NAME, "")
            .path("/api")
            .maxAge(0)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .build()
    }

    /**
     * Delete a user GitHub id cookie.
     */
    private fun deleteGithubIdCookie(): ResponseCookie {
        return ResponseCookie.from(GITHUB_ID_COOKIE_NAME, "")
            .path("/api")
            .maxAge(0)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .build()
    }

    /**
     * Generate a random token.
     */
    private fun generateRandomToken(): String {
        return UUID.randomUUID().toString()
    }
}
