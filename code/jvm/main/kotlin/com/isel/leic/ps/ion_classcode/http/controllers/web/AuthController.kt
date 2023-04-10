package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.input.OtpInputModel
import com.isel.leic.ps.ion_classcode.domain.input.StudentInput
import com.isel.leic.ps.ion_classcode.domain.input.TeacherInput
import com.isel.leic.ps.ion_classcode.domain.input.request.ApplyInput
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
import com.isel.leic.ps.ion_classcode.http.model.output.ClientToken
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserEmail
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserInfo
import com.isel.leic.ps.ion_classcode.http.model.output.OAuthState
import com.isel.leic.ps.ion_classcode.http.model.output.StatusOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.OutboxServices
import com.isel.leic.ps.ion_classcode.http.services.OutboxServicesError
import com.isel.leic.ps.ion_classcode.http.services.RequestServices
import com.isel.leic.ps.ion_classcode.http.services.StudentServices
import com.isel.leic.ps.ion_classcode.http.services.StudentServicesError
import com.isel.leic.ps.ion_classcode.http.services.UserServices
import com.isel.leic.ps.ion_classcode.http.services.UserServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
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
const val PENDING_STUDENT_COOKIE_NAME = "PendingStudent"
const val STATE_COOKIE_NAME = "userState"
const val STATE_COOKIE_PATH = Uris.CALLBACK_PATH
const val HALF_HOUR: Long = 60 * 30
const val FULL_DAY: Long = 60 * 60 * 24
const val AUTHORIZATION_COOKIE_NAME = "Session"

@RestController
class AuthController(
    private val okHttp: OkHttp,
    private val userServices: UserServices,
    private val studentServices: StudentServices,
    private val requestServices: RequestServices,
    private val outboxServices: OutboxServices,
) {

    @GetMapping(Uris.AUTH_TEACHER_PATH)
    fun authTeacher(): ResponseEntity<Any> {
        val state = generateUserState()
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.SET_COOKIE, state.cookie.toString())
            .header(HttpHeaders.SET_COOKIE, generateUserPosition(TEACHER_COOKIE_NAME).toString())
            .header(HttpHeaders.LOCATION, "$GITHUB_BASE_URL${GITHUB_OAUTH_URI(GITHUB_TEACHER_SCOPE, state.value)}")
            .build()
    }

    @GetMapping(Uris.AUTH_STUDENT_PATH)
    fun authStudent(): ResponseEntity<Any> {
        val state = generateUserState()
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.SET_COOKIE, state.cookie.toString())
            .header(HttpHeaders.SET_COOKIE, generateUserPosition(STUDENT_COOKIE_NAME).toString())
            .header(HttpHeaders.LOCATION, "$GITHUB_BASE_URL${GITHUB_OAUTH_URI(GITHUB_STUDENT_SCOPE, state.value)}")
            .build()
    }

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
                    val cookie = generateSessionCookie(userInfo.value.token)
                    return ResponseEntity
                        .status(Status.REDIRECT)
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .header(HttpHeaders.LOCATION, Uris.MENU_PATH)
                        .body(EMPTY_REQUEST)
                } else {
                    when (position) {
                        "Teacher" -> siren(StatusOutputModel("Needing approval", "Wait for approval from other teachers")) {
                            link(href = Uris.homeUri(), rel = LinkRelation("home"))
                            link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                        }
                        else -> siren(StatusOutputModel("Needing action.", "Verify your email to proceed with the verification or register using your school id.")) {
                            link(href = Uris.homeUri(), rel = LinkRelation("home"))
                            link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                            action("register", href = Uris.authUriRegister(), method = HttpMethod.POST, type = "application/json") {
                                numberField("schoolId")
                            }
                            action("verify", href = Uris.authUriRegisterVerification(), method = HttpMethod.POST, type = "application/json") {
                                numberField("otp")
                            }
                        }
                    }
                }
            }
            is Either.Left -> {
                val userEmail = fetchUserEmails(accessToken.access_token).first { it.primary }
                if (position == "Teacher") {
                    when (
                        val user = userServices.createTeacher(
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
                            requestServices.createApplyRequest(ApplyInput(creator = user.value.id, composite = null))
                            siren(StatusOutputModel("Needing approval", "Wait for approval from other teachers")) {
                                link(href = Uris.homeUri(), rel = LinkRelation("home"))
                                link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                            }
                        }
                        is Either.Left -> problemUser(user.value)
                    }
                } else {
                    when (
                        val user = userServices.createStudent(
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
                            val cookie = generatePendingStudentCookie(user.value.token)
                            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString())
                            siren(StatusOutputModel("Needing school identifier.", "Register using your school id now.")) {
                                link(href = Uris.homeUri(), rel = LinkRelation("home"))
                                link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                                action("register", href = Uris.authUriRegister(), method = HttpMethod.POST, type = "application/json") {
                                    numberField("schoolId")
                                }
                            }
                        }
                        is Either.Left -> problemUser(user.value)
                    }
                }
            }
        }
    }

    @PostMapping(Uris.AUTH_REGISTER_PATH)
    fun authRegisterStudent(
        @CookieValue("PendingStudent") token: String,
        @RequestBody input: SchoolIdInputModel,
    ): ResponseEntity<*> {
        return when (val user = userServices.checkAuthentication(token)) {
            is Either.Right -> {
                when (val student = studentServices.updateStudent(user.value.id, input.schoolId)) {
                    is Either.Right -> {
                        when (val userOutbox = outboxServices.createUserVerification(user.value.id)) {
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
                    is Either.Left -> problemStudent(student.value)
                }
            }
            is Either.Left -> problemUser(user.value)
        }
    }

    @PostMapping(Uris.AUTH_REGISTER_VERIFICATION_PATH, produces = ["application/vnd.siren+json"])
    fun authRegisterVerifyStudent(
        @CookieValue("PendingStudent") token: String,
        @RequestBody
        input: OtpInputModel,
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        return when (val user = userServices.checkAuthentication(token)) {
            is Either.Right -> {
                when (val checkOTP = outboxServices.checkOtp(user.value.id, input.otp)) {
                    is Either.Right -> {
                        val deleteCookie = removePendingStudentCookie()
                        val cookie = generateSessionCookie(token)
                        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString())
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

    @PostMapping(Uris.LOGOUT)
    fun logout(
        response: HttpServletResponse,
    ): ResponseEntity<Any> {
        val cookie = ResponseCookie.from(AUTHORIZATION_COOKIE_NAME, "")
            .httpOnly(true)
            .sameSite("Strict")
            .secure(true)
            .maxAge(0)
            .path("/api")
            .build()
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        return ResponseEntity
            .status(Status.REDIRECT)
            .header(HttpHeaders.LOCATION, Uris.HOME)
            .build()
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

    private fun generateSessionCookie(token: String): ResponseCookie {
        return ResponseCookie.from(AUTHORIZATION_COOKIE_NAME, AESEncrypt.encrypt(token))
            .httpOnly(true)
            .sameSite("Strict")
            .secure(true)
            .maxAge(FULL_DAY)
            .path("/api")
            .build()
    }

    private fun generatePendingStudentCookie(token: String): ResponseCookie {
        return ResponseCookie.from(PENDING_STUDENT_COOKIE_NAME, token)
            .httpOnly(true)
            .sameSite("Strict")
            .secure(true)
            .maxAge(FULL_DAY)
            .path("/api")
            .build()
    }

    private fun removePendingStudentCookie(): ResponseCookie {
        return ResponseCookie.from(PENDING_STUDENT_COOKIE_NAME, "")
            .httpOnly(true)
            .sameSite("Strict")
            .secure(true)
            .maxAge(0)
            .path("/api")
            .build()
    }

    private fun generateUserPosition(position: String): ResponseCookie {
        return ResponseCookie.from("position", position)
            .path(STATE_COOKIE_PATH)
            .maxAge(HALF_HOUR)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .build()
    }

    private suspend fun fetchAccessToken(code: String): ClientToken {
        val request = Request.Builder().url("$GITHUB_BASE_URL${GITHUB_ACCESS_TOKEN_URI(code)}")
            .addHeader("Accept", "application/json")
            .post(EMPTY_REQUEST)
            .build()

        return okHttp.makeCallToObject(request)
    }

    private suspend fun fetchUserInfo(accessToken: String): GitHubUserInfo {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USERINFO_URI")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")
            .build()

        return okHttp.makeCallToObject(request)
    }

    private suspend fun fetchUserEmails(accessToken: String): List<GitHubUserEmail> {
        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USERMAILS_URI")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/vnd.github+json")
            .build()

        return okHttp.makeCallToList(request)
    }

    private fun generateRandomToken(): String {
        return UUID.randomUUID().toString()
    }

    private fun problemUser(error: UserServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            UserServicesError.InvalidData -> Problem.invalidInput
            UserServicesError.UserNotFound -> Problem.notFound
            UserServicesError.UserNotAuthenticated -> Problem.unauthenticated
            UserServicesError.ErrorCreatingUser -> Problem.internalError
            UserServicesError.InvalidGithubId -> Problem.invalidInput
            UserServicesError.InvalidBearerToken -> Problem.invalidInput
            UserServicesError.GithubIdInUse -> Problem.conflict
            UserServicesError.TokenInUse -> Problem.conflict
            UserServicesError.EmailInUse -> Problem.conflict
            UserServicesError.GithubUserNameInUse -> Problem.conflict
            UserServicesError.GithubTokenInUse -> Problem.conflict
            UserServicesError.SchoolIdInUse -> Problem.conflict
        }
    }

    private fun problemOtp(error: OutboxServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            OutboxServicesError.OtpExpired -> Problem.gone
            OutboxServicesError.OtpDifferent -> Problem.badRequest
            OutboxServicesError.OtpNotFound -> Problem.notFound
            OutboxServicesError.UserNotFound -> Problem.notFound
            OutboxServicesError.EmailNotSent -> Problem.internalError
            OutboxServicesError.ErrorCreatingRequest -> Problem.internalError
            else -> Problem.forbidden
        }
    }

    private fun problemStudent(error: StudentServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            is StudentServicesError.UserNotFound -> Problem.userNotFound
            is StudentServicesError.CourseNotFound -> Problem.courseNotFound
        }
    }
}
