package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.domain.input.OtpInputModel
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.http.GITHUB_BASE_URL
import com.isel.leic.ps.ion_classcode.http.GITHUB_OAUTH_URI
import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.SchoolIdInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AuthRedirect
import com.isel.leic.ps.ion_classcode.http.model.output.AuthStateOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.OAuthState
import com.isel.leic.ps.ion_classcode.http.model.output.RegisterOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.StatusOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.services.GithubServices
import com.isel.leic.ps.ion_classcode.http.services.OutboxServices
import com.isel.leic.ps.ion_classcode.http.services.OutboxServicesError
import com.isel.leic.ps.ion_classcode.services.StudentServices
import com.isel.leic.ps.ion_classcode.services.TeacherServices
import com.isel.leic.ps.ion_classcode.http.services.UserServices
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.SirenModel
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import com.isel.leic.ps.ion_classcode.utils.Result
import com.isel.leic.ps.ion_classcode.utils.cypher.AESDecrypt
import com.isel.leic.ps.ion_classcode.utils.cypher.AESEncrypt
import jakarta.servlet.http.HttpServletRequest
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
import java.util.UUID

const val ORG_NAME = "test-project-isel"
const val GITHUB_TEACHER_SCOPE = "read:org user:email repo"
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

//const val NGROK_DOMAIN = "947b-2001-818-e975-8500-5c24-94b1-29c4-34e2.ngrok-free.app"
//const val NGROK_URI = "https://$NGROK_DOMAIN"

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
    @GetMapping(Uris.AUTH_TEACHER_PATH)
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
    @GetMapping(Uris.AUTH_STUDENT_PATH)
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
    @GetMapping(Uris.AUTH_STATE_PATH)
    fun authState(
        user: User,
    ): ResponseEntity<SirenModel<AuthStateOutputModel>> {
        return siren(AuthStateOutputModel(user, true)) {
            clazz("auth")
            link(rel = LinkRelation("self"), href = Uris.AUTH_STATE_PATH)
        }
    }

    /**
     * Callback from the OAuth2 provider.
     * It fetches the access token and the user info.
     * Check if the user is created and verified, and computes accordingly.
     */
    @GetMapping(Uris.CALLBACK_PATH, produces = ["application/vnd.siren+json"])
    suspend fun callback(
        @RequestParam code: String,
        @RequestParam state: String,
        @CookieValue userState: String,
        @CookieValue position: String,
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        if (state != userState) return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/error/callback")
            .body(EMPTY_REQUEST)
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
                                .header(HttpHeaders.LOCATION, "http://localhost:3000/menu/callback/student")
                                .body(EMPTY_REQUEST)
                        }
                        userInfo.value is Teacher && position == TEACHER_COOKIE_NAME -> {
                            when (teacherServices.updateTeacherGithubToken(userInfo.value.id, accessToken.access_token)) {
                                is Result.Problem -> ResponseEntity
                                    .status(Status.REDIRECT)
                                    .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/error/callback")
                                    .body(EMPTY_REQUEST)
                                is Result.Success -> {
                                    val cookie = generateSessionCookie(userInfo.value.token)
                                    ResponseEntity
                                        .status(Status.REDIRECT)
                                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                        .header(HttpHeaders.LOCATION, "http://localhost:3000/menu/callback/teacher")
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
                                .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/fail/callback")
                                .body(EMPTY_REQUEST)
                        }
                    }
                } else {
                    if (position == TEACHER_COOKIE_NAME) {
                        val cookie = generateGithubIdCookie(userGithubInfo.id)
                        ResponseEntity
                            .status(Status.REDIRECT)
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/status")
                            .body(EMPTY_REQUEST)
                    } else {
                        val cookie = generateGithubIdCookie(userGithubInfo.id)
                        ResponseEntity
                            .status(Status.REDIRECT)
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/verify")
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
                                githubUsername =  userGithubInfo.login,
                                githubId = userGithubInfo.id,
                                token = generateRandomToken(),
                                name = userGithubInfo.name,
                                githubToken = accessToken.access_token,
                            )
                        )
                    ) {
                        is Result.Success -> {
                            val cookie = generateGithubIdCookie(userGithubInfo.id)
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/create/callback/teacher")
                                .body(EMPTY_REQUEST)
                        }
                        is Result.Problem -> ResponseEntity
                            .status(Status.REDIRECT)
                            .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/error/callback")
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
                                name = userGithubInfo.name,
                            )
                        )
                    ) {
                        is Result.Success -> {
                            val cookie = generateGithubIdCookie(userGithubInfo.id)
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/create/callback/student")
                                .body(EMPTY_REQUEST)
                        }
                        is Result.Problem -> ResponseEntity
                            .status(Status.REDIRECT)
                            .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/error/callback")
                            .body(EMPTY_REQUEST)
                    }
                }
            }
        }
    }

    @GetMapping(Uris.AUTH_REGISTER_PATH)
    fun getRegisterInfo(
        @CookieValue userGithubId: String,
    ): ResponseEntity<*> {
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val userInfo = userServices.getPendingUserByGithubId(githubId)) {
            is Result.Success -> siren(RegisterOutputModel(userInfo.value.name, userInfo.value.email, userInfo.value.githubUsername)) {
                clazz("registerInfo")
                link(rel = LinkRelation("self"), href = Uris.AUTH_REGISTER_PATH)
            }
            is Result.Problem -> userServices.problem(userInfo.value)
        }
    }

    @PostMapping(Uris.AUTH_REGISTER_TEACHER_PATH)
    fun createTeacher(
        @CookieValue userGithubId: String,
        @CookieValue position: String,
    ): ResponseEntity<*> {
        if (position != TEACHER_COOKIE_NAME) return Problem.badRequest
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val teacher = teacherServices.createTeacher(githubId)) {
            is Result.Success -> siren(StatusOutputModel("User Register", "Verify the status of your account")) {
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
                    is Either.Right -> siren(StatusOutputModel("Verify user", "Verify your email to proceed with the verification")) {
                        clazz("registerStudent")
                        action(title = "registerStudent", href = Uris.AUTH_REGISTER_STUDENT_PATH, method = HttpMethod.POST, type = "application/json", block = {
                            numberField("schoolId")
                        })
                    }
                    is Either.Left -> when (userOutbox.value) {
                        is OutboxServicesError.CooldownNotExpired -> siren(StatusOutputModel(
                                "On cooldown",
                                "You are on cooldown, try again in ${userOutbox.value.cooldown} seconds",
                            )) {
                            clazz("registerStudent")
                            action(title = "registerStudent", href = Uris.AUTH_REGISTER_STUDENT_PATH, method = HttpMethod.POST, type = "application/json", block = {
                                numberField("schoolId")
                            })
                        }
                        else -> problemOtp(userOutbox.value)
                    }
                }
            }
            is Result.Problem -> studentServices.problem(student.value)
        }
    }

    @GetMapping(Uris.AUTH_STATUS_PATH)
    fun getStatus(
        @CookieValue position: String,
        @CookieValue userGithubId: String,
    ): ResponseEntity<*> {
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val userInfo = userServices.getUserByGithubId(githubId)) {
            is Result.Success -> {
                if (userInfo.value.isCreated) {
                    siren(StatusOutputModel(
                        "You are now eligible to use the application.",
                        "Return to home to authenticate yourself.",
                    )) {
                        clazz("status")
                        link(rel = LinkRelation("self"), href = Uris.AUTH_STATUS_PATH)
                    }
                } else {
                    when (position) {
                        TEACHER_COOKIE_NAME -> siren(StatusOutputModel(
                            "Needing approval.",
                            "Wait for approval from other teachers.",
                        )) {
                            clazz("status")
                            link(rel = LinkRelation("self"), href = Uris.AUTH_STATUS_PATH)
                        }
                        else -> siren(StatusOutputModel(
                            "Needing action.",
                            "Verify your email to proceed with the verification or register using your school id.",
                        )) {
                            clazz("status")
                            link(rel = LinkRelation("self"), href = Uris.AUTH_STATUS_PATH)
                        }
                    }
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
                is Either.Right -> {
                    val cookie = generateSessionCookie(user.value.token)
                    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString())
                    response.setHeader(HttpHeaders.SET_COOKIE, generateUserPosition(STUDENT_COOKIE_NAME).toString())
                    siren(StatusOutputModel("User verified.", "User verified successfully, you can navigate to menu.")) {
                        clazz("verifyStudent")
                        action(title = "verify", href = Uris.AUTH_REGISTER_VERIFICATION_PATH, method = HttpMethod.POST, type = "application/json", block = {
                            numberField("otp")
                        })
                    }
                }
                is Either.Left -> problemOtp(checkOTP.value)
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
                is Either.Right -> {
                    siren(StatusOutputModel("Email sent.", "Email sent successfully, check your email.")) {
                        clazz("resendEmail")
                        action(title = "self", href = Uris.AUTH_RESEND_EMAIL_PATH, method = HttpMethod.POST, type = "application/json", block = {})
                    }
                }
                is Either.Left -> problemOtp(resendEmail.value)
            }
            is Result.Problem -> userServices.problem(user.value)
        }
    }

    /**
     * Logout the user.
     */
    @PostMapping(Uris.LOGOUT)
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        val requestCookies = request.cookies
        requestCookies.forEach { cookie ->
            cookie.maxAge = 0
            cookie.path = "/api"
            response.addCookie(cookie)
        }
        return siren(null) {
            clazz("logout")
            action(title = "logout", href = Uris.LOGOUT, method = HttpMethod.POST, type = "application/json", block = {})
        }
    }

    private fun generateUserState(): OAuthState {
        val state = UUID.randomUUID().toString()
        val cookie = ResponseCookie.from(STATE_COOKIE_NAME, state)
            .path(STATE_COOKIE_PATH)
            .maxAge(HALF_HOUR)
            //.domain(NGROK_DOMAIN)
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
            .httpOnly(true)
            .sameSite("Strict")
            .secure(true)
            .maxAge(FULL_DAY)
            .path("/api")
            .build()
    }

    /**
     * Delete a session cookie.
     */
    private fun deleteSessionCookie(): ResponseCookie {
        return ResponseCookie.from(AUTHORIZATION_COOKIE_NAME, "")
            .httpOnly(true)
            .sameSite("Strict")
            .secure(true)
            .maxAge(0)
            .path("/api")
            .build()
    }

    /**
     * Create a user position cookie.
     */
    private fun generateUserPosition(position: String): ResponseCookie {
        return ResponseCookie.from(POSITION_COOKIE_NAME, position)
            .path("/api")
            .maxAge(HALF_HOUR)
            //.domain(NGROK_DOMAIN)
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
            .sameSite("None")
            .build()
    }

    /**
     * Delete a user GitHub id cookie.
     */
    private fun deleteGithubIdCookie(): ResponseCookie {
        return ResponseCookie.from(GITHUB_ID_COOKIE_NAME, "")
            .httpOnly(true)
            .sameSite("Strict")
            .secure(true)
            .maxAge(0)
            .path("/api")
            .build()
    }

    /**
     * Generate a random token.
     */
    private fun generateRandomToken(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * Function to handle errors about the OTP.
     */
    private fun problemOtp(error: OutboxServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            OutboxServicesError.OtpExpired -> Problem.gone
            OutboxServicesError.OtpDifferent -> Problem.invalidInput
            OutboxServicesError.OtpNotFound -> Problem.notFound
            OutboxServicesError.UserNotFound -> Problem.notFound
            OutboxServicesError.EmailNotSent -> Problem.internalError
            OutboxServicesError.ErrorCreatingRequest -> Problem.internalError
            OutboxServicesError.InvalidInput -> Problem.invalidInput
            else -> Problem.cooldown((error as OutboxServicesError.CooldownNotExpired).cooldown)
        }
    }
}
