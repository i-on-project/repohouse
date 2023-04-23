package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.input.OtpInputModel
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.http.GITHUB_ACCESS_TOKEN_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_API_BASE_URL
import com.isel.leic.ps.ion_classcode.http.GITHUB_BASE_URL
import com.isel.leic.ps.ion_classcode.http.GITHUB_OAUTH_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_USERINFO_URI
import com.isel.leic.ps.ion_classcode.http.GITHUB_USERMAILS_URI
import com.isel.leic.ps.ion_classcode.http.OkHttp
import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.makeCallToList
import com.isel.leic.ps.ion_classcode.http.makeCallToObject
import com.isel.leic.ps.ion_classcode.http.model.input.SchoolIdInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.AuthRedirect
import com.isel.leic.ps.ion_classcode.http.model.output.ClientToken
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserEmail
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserInfo
import com.isel.leic.ps.ion_classcode.http.model.output.OAuthState
import com.isel.leic.ps.ion_classcode.http.model.output.RegisterOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.StatusOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.OutboxServices
import com.isel.leic.ps.ion_classcode.http.services.OutboxServicesError
import com.isel.leic.ps.ion_classcode.http.services.UserServices
import com.isel.leic.ps.ion_classcode.http.services.UserServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import com.isel.leic.ps.ion_classcode.utils.cypher.AESDecrypt
import com.isel.leic.ps.ion_classcode.utils.cypher.AESEncrypt
import jakarta.servlet.http.HttpServletResponse
import okhttp3.Request
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

/**
 * This controller is responsible for the authentication of the users.
 * It uses the OAuth2 protocol to authenticate the users.
 * It also handles the callback from the OAuth2 provider.
 */
@RestController
class AuthController(
    private val okHttp: OkHttp,
    private val userServices: UserServices,
    private val outboxServices: OutboxServices,
) {

    /**
     * Teacher authentication with the respective scope.
     */
    @GetMapping(Uris.AUTH_TEACHER_PATH)
    fun authTeacher(
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val state = generateUserState()
        response.addHeader(HttpHeaders.SET_COOKIE, state.cookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, generateUserPosition(TEACHER_COOKIE_NAME).toString())
        return siren(AuthRedirect(url = "$GITHUB_BASE_URL${GITHUB_OAUTH_URI(GITHUB_TEACHER_SCOPE, state.value)}")) {
            link(href = Uris.authUriTeacher(), rel = LinkRelation("self"))
            link(href = Uris.callbackUri(), rel = LinkRelation("authCallback"))
        }
    }

    /**
     * Student authentication with the respective scope.
     */
    @GetMapping(Uris.AUTH_STUDENT_PATH)
    fun authStudent(
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val state = generateUserState()
        response.addHeader(HttpHeaders.SET_COOKIE, state.cookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, generateUserPosition(STUDENT_COOKIE_NAME).toString())
        return siren(AuthRedirect(url = "$GITHUB_BASE_URL${GITHUB_OAUTH_URI(GITHUB_STUDENT_SCOPE, state.value)}")) {
            link(href = Uris.authUriStudent(), rel = LinkRelation("self"))
            link(href = Uris.callbackUri(), rel = LinkRelation("authCallback"))
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
        if (state != userState) return Problem.stateMismatch
        val accessToken = fetchAccessToken(code)
        val userGithubInfo = fetchUserInfo(accessToken.access_token)
        return when (val userInfo = userServices.getUserByGithubId(userGithubInfo.id)) {
            is Either.Right -> {
                if (userInfo.value.isCreated) {
                    when {
                        userInfo.value is Student && position == "Student" -> {
                            val cookie = generateSessionCookie(userInfo.value.token)
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .header(HttpHeaders.LOCATION, "http://localhost:3000/menu/callback/student")
                                .body(EMPTY_REQUEST)
                        }
                        userInfo.value is Teacher && position == "Teacher" -> {
                            when (val update = userServices.updateTeacherGithubToken(userInfo.value.id, accessToken.access_token)) {
                                is Either.Left -> problemUser(update.value)
                                is Either.Right -> {
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
                    if (position == "Teacher") {
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
            is Either.Left -> {
                val userEmail = fetchUserEmails(accessToken.access_token).first { it.primary }
                if (position == "Teacher") {
                    when (
                        val user = userServices.createPendingTeacher(
                            TeacherInput(
                                userEmail.email,
                                userGithubInfo.login,
                                userGithubInfo.id,
                                generateRandomToken(),
                                userGithubInfo.name,
                                accessToken.access_token,
                            ),
                        )
                    ) {
                        is Either.Right -> {
                            val cookie = generateGithubIdCookie(userGithubInfo.id)
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/create/callback/teacher")
                                .body(EMPTY_REQUEST)
                        }

                        is Either.Left -> problemUser(user.value)
                    }
                } else {
                    when (
                        val user = userServices.createPendingStudent(
                            StudentInput(
                                email = userEmail.email,
                                githubUsername = userGithubInfo.login,
                                githubId = userGithubInfo.id,
                                token = generateRandomToken(),
                                name = userGithubInfo.name,
                            ),
                        )
                    ) {
                        is Either.Right -> {
                            val cookie = generateGithubIdCookie(userGithubInfo.id)
                            ResponseEntity
                                .status(Status.REDIRECT)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .header(HttpHeaders.LOCATION, "http://localhost:3000/auth/create/callback/student")
                                .body(EMPTY_REQUEST)
                        }

                        is Either.Left -> problemUser(user.value)
                    }
                }
            }
        }
    }

    @GetMapping(Uris.AUTH_REGISTER_PATH)
    fun getRegisterInfo(
        @CookieValue userGithubId: String
    ): ResponseEntity<*> {
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val userInfo = userServices.getPendingUserByGithubId(githubId)) {
            is Either.Right -> {
                siren(RegisterOutputModel(userInfo.value.name, userInfo.value.email, userInfo.value.githubUsername)) {}
            }
            is Either.Left -> problemUser(userInfo.value)
        }
    }

    @PostMapping(Uris.AUTH_REGISTER_TEACHER_PATH)
    fun createTeacher(
        @CookieValue userGithubId: String,
        @CookieValue position: String
    ): ResponseEntity<*> {
        if (position != "Teacher") return Problem.badRequest
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val teacher = userServices.createTeacher(githubId)) {
            is Either.Right -> {
                siren(StatusOutputModel("User Register", "Verify the status of your account")) {
                    link(href = Uris.authStatusUri(), rel = LinkRelation("status"))
                    link(href = Uris.homeUri(), rel = LinkRelation("home"))
                    link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                }
            }
            is Either.Left -> problemUser(teacher.value)
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
        if (position != "Student") return Problem.badRequest
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val student = userServices.createStudent(githubId,input.schoolId)) {
            is Either.Right -> {
                when (val userOutbox = outboxServices.createUserVerification(student.value.id)) {
                    is Either.Right -> siren(StatusOutputModel("Verify user", "Verify your email to proceed with the verification")) {
                        link(href = Uris.homeUri(), rel = LinkRelation("home"))
                        link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                        action("verify", href = Uris.authUriRegisterVerification(), method = HttpMethod.POST, type = "application/json") {
                            numberField("otp")
                        }
                    }
                    is Either.Left ->
                        when (userOutbox.value) {
                            is OutboxServicesError.CooldownNotExpired -> siren(
                                StatusOutputModel(
                                    "On cooldown",
                                    "You are on cooldown, try again in ${userOutbox.value.cooldown} seconds",
                                ),
                            ) {
                                link(href = Uris.homeUri(), rel = LinkRelation("home"))
                                link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                            }
                            else -> problemOtp(userOutbox.value)
                        }
                }
            }
            is Either.Left -> problemUser(student.value)
        }
    }

    @GetMapping(Uris.AUTH_STATUS_PATH)
    fun getStatus(
        @CookieValue position: String,
        @CookieValue userGithubId: String
    ): ResponseEntity<*> {
        val githubId = AESDecrypt.decrypt(userGithubId).toLong()
        return when (val userInfo = userServices.getUserByGithubId(githubId)) {
            is Either.Right -> {
                if (userInfo.value.isCreated){
                    val cookie = generateSessionCookie(userInfo.value.token)
                    ResponseEntity
                        .status(Status.REDIRECT)
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .header(HttpHeaders.LOCATION, "http://localhost:3000/menu/callback")
                        .body(EMPTY_REQUEST)
                }else {
                    when (position) {
                        "Teacher" -> siren(
                            StatusOutputModel(
                                "Needing approval",
                                "Wait for approval from other teachers"
                            )
                        ) {}
                        else -> siren(
                            StatusOutputModel(
                                "Needing action.",
                                "Verify your email to proceed with the verification or register using your school id."
                            )
                        ) {}
                    }
                }
            }
            is Either.Left -> {
                problemUser(userInfo.value)
            }
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
        return when (val user = userServices.getUserByGithubId(githubId = githubId)) {
            is Either.Right -> {
                when (val checkOTP = outboxServices.checkOtp(user.value.id, input.otp)) {
                    is Either.Right -> {
                        val cookie = generateSessionCookie(user.value.token)
                        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString())
                        response.setHeader(HttpHeaders.SET_COOKIE, generateUserPosition(STUDENT_COOKIE_NAME).toString())
                        siren(StatusOutputModel("User verified.", "User verified successfully, you can navigate to menu.")) {
                            link(href = Uris.menuUri(), rel = LinkRelation("menu"), needAuthentication = true)
                            link(href = Uris.homeUri(), rel = LinkRelation("home"))
                            link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                        }
                    }
                    is Either.Left -> problemOtp(checkOTP.value)
                }
            }
            is Either.Left -> problemUser(user.value)
        }
    }

    /**
     * Logout the user.
     */
    @PostMapping(Uris.LOGOUT)
    fun logout(
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        val authorizationCookie = deleteSessionCookie()
        val githubIdCookie = deleteGithubIdCookie()
        response.setHeader(HttpHeaders.SET_COOKIE, authorizationCookie.toString())
        response.setHeader(HttpHeaders.SET_COOKIE, githubIdCookie.toString())
        return siren(null) {}
    }

    private fun generateUserState(): OAuthState {
        val state = UUID.randomUUID().toString()
        val cookie = ResponseCookie.from(STATE_COOKIE_NAME, state)
            .path(STATE_COOKIE_PATH)
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
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .build()
    }

    /**
     * Create a user GitHub id cookie.
     */
    private fun generateGithubIdCookie(gitHubId:Long): ResponseCookie {
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
     * Method to fetch the user access token from GitHub.
     */
    private suspend fun fetchAccessToken(code: String): ClientToken {
        val request = Request.Builder().url("$GITHUB_BASE_URL${GITHUB_ACCESS_TOKEN_URI(code)}")
            .addHeader("Accept", "application/json")
            .post(EMPTY_REQUEST)
            .build()

        return okHttp.makeCallToObject(request)
    }

    /**
     * Method to fetch the user info from GitHub.
     */
    private suspend fun fetchUserInfo(accessToken: String): GitHubUserInfo {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USERINFO_URI")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()

        return okHttp.makeCallToObject(request)
    }

    /**
     * Method to fetch the user emails from GitHub.
     */
    private suspend fun fetchUserEmails(accessToken: String): List<GitHubUserEmail> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USERMAILS_URI")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/vnd.github+json")
            .build()

        return okHttp.makeCallToList(request)
    }

    /**
     * Generate a random token.
     */
    private fun generateRandomToken(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * Function to handle errors about the user.
     */
    private fun problemUser(error: UserServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            UserServicesError.InvalidData -> Problem.invalidInput
            UserServicesError.UserNotFound -> Problem.notFound
            UserServicesError.UserNotAuthenticated -> Problem.unauthenticated
            UserServicesError.ErrorCreatingUser -> Problem.internalError
            UserServicesError.InvalidGithubId -> Problem.internalError
            UserServicesError.InvalidToken -> Problem.unauthenticated
            UserServicesError.GithubIdInUse -> Problem.internalError
            UserServicesError.TokenInUse -> Problem.internalError
            UserServicesError.EmailInUse -> Problem.internalError
            UserServicesError.GithubUserNameInUse -> Problem.internalError
            UserServicesError.GithubTokenInUse -> Problem.internalError
            UserServicesError.SchoolIdInUse -> Problem.conflict
        }
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
