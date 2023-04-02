package com.isel.leic.ps.ion_classcode.http.controllers

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
import com.isel.leic.ps.ion_classcode.http.model.output.ErrorOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserEmail
import com.isel.leic.ps.ion_classcode.http.model.output.GitHubUserInfo
import com.isel.leic.ps.ion_classcode.http.model.output.InfoOutputModel
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

const val ORG_NAME = "test-project-isel"
const val GITHUB_TEACHER_SCOPE = "read:org%20user:email"
const val GITHUB_STUDENT_SCOPE = "repo%20user:email"

const val STUDENT_COOKIE_NAME = "Student"
const val TEACHER_COOKIE_NAME = "Teacher"
const val STATE_COOKIE_NAME = "userState"
const val STATE_COOKIE_PATH = Uris.CALLBACK_PATH
const val HALF_HOUR: Long = 60 * 30
const val APP_COOKIE_NAME = "Session"

@RestController
class AuthController(
    private val okHttp: OkHttp,
    private val userServices: UserServices,
    private val studentServices: StudentServices,
    private val requestServices: RequestServices,
    private val outboxServices: OutboxServices
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

    @GetMapping(Uris.CALLBACK_PATH)
    suspend fun callback(
        @RequestParam code: String,
        @RequestParam state: String,
        @CookieValue userState: String,
        @CookieValue position: String,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        if (state != userState) return Problem.stateMismatch
        val accessToken = fetchAccessToken(code)
        val userGithubInfo = fetchUserInfo(accessToken.access_token)
        return when(val userInfo = userServices.getUserByGithubId(userGithubInfo.id)) {
            is Either.Right -> {
                if (userInfo.value.isCreated) {
                    val cookie = ResponseCookie.from(APP_COOKIE_NAME, AESEncrypt.encrypt(userInfo.value.token))
                        .httpOnly(true)
                        .sameSite("Strict")
                        .secure(true)
                        .maxAge(60 * 60 * 24)
                        .path("/api")
                        .build()

                    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
                    siren(StatusOutputModel("User logged in", "Redirect to menu page")) {
                        link(href = Uris.menuUri(), rel = LinkRelation("menu"), needAuthentication = true)
                        link(href = Uris.logoutUri(), rel = LinkRelation("logout"), needAuthentication = true)
                        link(href = Uris.homeUri(), rel = LinkRelation("home"))
                        link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                    }
                } else {
                    val userId = userInfo.value.id
                    siren(StatusOutputModel("Check user status", "Redirect to status page")) {
                        link(href = Uris.homeUri(), rel = LinkRelation("home"))
                        link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                        link(href = Uris.authStatusUri(userId), rel = LinkRelation("status"))
                    }
                }
            }
            is Either.Left -> {
                val userEmail = fetchUserEmails(accessToken.access_token).first { it.primary }
                if (position == "Teacher") {
                    when(val user = userServices.createTeacher(
                        TeacherInput(
                            userEmail.email,
                            userGithubInfo.login,
                            userGithubInfo.id,
                            generateRandomToken(),
                            userGithubInfo.name,
                            accessToken.access_token
                        )
                    )) {
                        is Either.Right -> {
                            requestServices.createApplyRequest(ApplyInput(user.value.id,null, user.value.id))
                            siren(StatusOutputModel("Check user status", "Redirect to status page")) {
                                link(href = Uris.homeUri(), rel = LinkRelation("home"))
                                link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                                link(href = Uris.authStatusUri(user.value.id), rel = LinkRelation("status"))
                            }
                        }
                        is Either.Left -> when (user.value) {
                            UserServicesError.InvalidData -> Problem.invalidInput
                            else -> Problem.internalError
                        }
                    }
                } else {
                    when(val user = userServices.createStudent(
                        StudentInput(
                            email = userEmail.email,
                            githubUsername = userGithubInfo.login,
                            githubId = userGithubInfo.id,
                            token = generateRandomToken(),
                            name= userGithubInfo.name
                        )
                    )) {
                        is Either.Right -> {
                            val cookie = ResponseCookie.from("PendingStudent", AESEncrypt.encrypt(user.value.token))
                                .httpOnly(true)
                                .sameSite("Strict")
                                .secure(true)
                                .maxAge(60 * 60 * 24)
                                .path("/api")
                                .build()

                            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString())
                            siren(StatusOutputModel("Register student user", "Redirect to register page")) {
                                link(href = Uris.homeUri(), rel = LinkRelation("home"))
                                link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                                link(href = Uris.authUriRegister(), rel = LinkRelation("register"))
                            }
                        }
                        is Either.Left -> when (user.value) {
                            UserServicesError.InvalidData -> Problem.invalidInput
                            else -> Problem.internalError
                        }
                    }
                }
            }
        }
    }

    @GetMapping(Uris.AUTH_REGISTER_PATH)
    fun authRegisterStudentPage(
        @CookieValue("PendingStudent") token: String
    ): ResponseEntity<*> {
        return siren(InfoOutputModel("Register student", "Please fill the form below with you school identification to register as a student")){
            link(href = Uris.homeUri(), rel = LinkRelation("home"))
            link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
            link(href = Uris.authUriRegister(), rel = LinkRelation("self"))
            action("register", href = Uris.authUriRegister(), method = HttpMethod.POST, type = "application/json") {
                numberField("school_id")
            }
        }
    }

    @PostMapping(Uris.AUTH_REGISTER_PATH)
    fun authRegisterStudent(
        @CookieValue("PendingStudent") token: String,
        @RequestBody input: SchoolIdInputModel
    ): ResponseEntity<*> {
        val decryptToken = AESDecrypt.decrypt(token)
        return when (val user = userServices.checkAuthentication(decryptToken)) {
            is Either.Right -> {
                 when (val student = studentServices.updateStudent(user.value.id, input.schoolId)) {
                    is Either.Right -> {
                        when (val userOutbox = outboxServices.createUserVerification(user.value.id)) {
                            is Either.Right -> siren(StatusOutputModel(
                                "User need verification",
                                "Check you email to proceed with the verification and go to the verify page"
                            )) {
                                link(href = Uris.homeUri(), rel = LinkRelation("home"))
                                link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                                link(href = Uris.authUriRegisterVerification(), rel = LinkRelation("verify"))
                                link(href = Uris.authUriRegister(), rel = LinkRelation("self"))
                            }
                            is Either.Left ->
                                when(userOutbox.value) {
                                    is OutboxServicesError.CooldownNotExpired -> siren(StatusOutputModel(
                                        "On cooldown",
                                        "You are on cooldown, try again in ${userOutbox.value.cooldown} seconds"
                                    )) {
                                        link(href = Uris.homeUri(), rel = LinkRelation("home"))
                                        link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                                        link(href = Uris.authUriRegisterVerification(), rel = LinkRelation("verify"))
                                        link(href = Uris.authUriRegister(), rel = LinkRelation("self"))
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

    @GetMapping(Uris.AUTH_REGISTER_VERIFICATION_PATH)
    fun authRegisterVerifyStudent(
        @CookieValue("PendingStudent") token: String,
    ): ResponseEntity<*> {
       return siren(StatusOutputModel("Send otp", "Check you email to proceed with the verification, entering the OTP")) {
            link(href = Uris.homeUri(), rel = LinkRelation("home"))
            link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
            link(href = Uris.authUriRegisterVerification(), rel = LinkRelation("self"))
            action("verify", href = Uris.authUriRegisterVerification(), method = HttpMethod.POST, type = "application/json"){
                numberField("otp")
            }
        }
    }

    @PostMapping(Uris.AUTH_REGISTER_VERIFICATION_PATH)
    fun authRegisterVerifyStudent(
        @CookieValue("PendingStudent") token: String,
        @RequestBody
        input: OtpInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val decryptToken = AESDecrypt.decrypt(token)
        return when (val user = userServices.checkAuthentication(decryptToken)) {
            is Either.Right -> {
                 when (val checkOTP = outboxServices.checkOtp(user.value.id, input.otp)) {
                    is Either.Right -> {
                        val deleteCookie = ResponseCookie.from("PendingStudent", "")
                            .httpOnly(true)
                            .sameSite("Strict")
                            .secure(true)
                            .maxAge(0)
                            .path("/api")
                            .build()

                        val cookie = ResponseCookie.from(APP_COOKIE_NAME, AESEncrypt.encrypt(decryptToken))
                            .httpOnly(true)
                            .sameSite("Strict")
                            .secure(true)
                            .maxAge(60 * 60 * 24)
                            .path("/api")
                            .build()

                        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString())
                        response.setHeader(HttpHeaders.SET_COOKIE, generateUserPosition(STUDENT_COOKIE_NAME).toString())
                        siren(StatusOutputModel(
                                "User verified",
                                "User verified successfully, you can navigate to menu"
                        )) {
                            link(href = Uris.menuUri(), rel = LinkRelation("menu"), needAuthentication = true)
                            link(href = Uris.homeUri(), rel = LinkRelation("home"))
                            link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                        }
                    }

                    is Either.Left -> {
                        when (checkOTP.value) {
                            is OutboxServicesError.OtpExpired -> {
                                siren(StatusOutputModel("OTP Expired", "OTP expired, please request new OTP")) {
                                    link(href = Uris.homeUri(), rel = LinkRelation("home"))
                                    link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                                    link(href = Uris.authUriRegister(), rel = LinkRelation("register"))
                                }
                            }

                            is OutboxServicesError.OtpDifferent -> {
                                siren(StatusOutputModel("OTP Not Valid", "OTP not valid, try again in 5 minutes")) {
                                    link(href = Uris.homeUri(), rel = LinkRelation("home"))
                                    link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                                    link(href = Uris.authUriRegister(), rel = LinkRelation("register"))
                                }
                            }

                            is OutboxServicesError.OtpNotFound -> {
                                siren(StatusOutputModel("OTP Not Found", "OTP not found, try again to register")) {
                                    link(href = Uris.homeUri(), rel = LinkRelation("home"))
                                    link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                                    link(href = Uris.authUriRegister(), rel = LinkRelation("register"))
                                }
                            }

                            else -> problemOtp(checkOTP.value)
                        }
                    }
                }
            }
            is Either.Left -> problemUser(user.value)
        }
    }


    @GetMapping(Uris.AUTH_STATUS_PATH)
    fun authStatus(
        @PathVariable("id") id: Int,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        return when (val user = userServices.getUserById(id)){
             is Either.Right -> {
                if(user.value.isCreated){
                    val cookie = ResponseCookie.from(
                        APP_COOKIE_NAME,
                        AESEncrypt.encrypt(user.value.token)
                    )
                        .httpOnly(true)
                        .sameSite("Strict")
                        .secure(true)
                        .maxAge(60 * 60 * 24)
                        .path("/api")
                        .build()
                    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString())

                    siren(StatusOutputModel("User created", "User created successfully, you can navigate to menu")) {
                        link(href = Uris.menuUri(), rel = LinkRelation("menu"), needAuthentication = true)
                        link(href = Uris.logoutUri(), rel = LinkRelation("logout"), needAuthentication = true)
                        link(href = Uris.authStatusUri(id), rel = LinkRelation("self"))
                        link(href = Uris.homeUri(), rel = LinkRelation("home"))
                        link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                    }
                } else {
                    siren(StatusOutputModel("User not yet created", "Pending user creation, please wait, if a student check your email for verification")) {
                        link(href = Uris.authStatusUri(id), rel = LinkRelation("self"))
                        link(href = Uris.homeUri(), rel = LinkRelation("home"))
                        link(href = Uris.creditsUri(), rel = LinkRelation("credits"))
                    }
                }
            }
            is Either.Left -> when (user.value) {
                UserServicesError.InvalidData -> Problem.invalidInput
                else -> Problem.notFound
            }
        }
    }

    @GetMapping(Uris.LOGOUT)
    fun logout(
        response: HttpServletResponse
    ): ResponseEntity<Any> {
        val cookie = ResponseCookie.from(APP_COOKIE_NAME, "")
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

    private suspend fun fetchUserEmails(accessToken: String):List<GitHubUserEmail>{
        val request = Request.Builder().url("$GITHUB_API_BASE_URL$GITHUB_USERMAILS_URI")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/vnd.github+json")
            .build()

        return okHttp.makeCallToList(request)
    }

    private fun generateRandomToken(): String {
        return UUID.randomUUID().toString()
    }

    private fun problemUser(error:UserServicesError):ResponseEntity<ErrorMessageModel>{
        return when(error){
            UserServicesError.InvalidData -> Problem.invalidInput
            UserServicesError.UserNotFound -> Problem.notFound
            UserServicesError.UserNotAuthenticated -> Problem.unauthenticated
            UserServicesError.ErrorCreatingUser -> Problem.internalError
            UserServicesError.InvalidGithubId -> Problem.invalidInput
        }
    }

    private fun problemOtp(error:OutboxServicesError):ResponseEntity<ErrorMessageModel>{
        return when(error){
            OutboxServicesError.OtpExpired -> Problem.gone
            OutboxServicesError.OtpDifferent -> Problem.badRequest
            OutboxServicesError.OtpNotFound -> Problem.notFound
            OutboxServicesError.UserNotFound -> Problem.notFound
            OutboxServicesError.EmailNotSent -> Problem.internalError
            OutboxServicesError.ErrorCreatingRequest -> Problem.internalError
            else -> Problem.forbidden //OutboxServicesError.CooldownNotExpired(cooldownTime)
        }
    }

    private fun problemStudent(error: StudentServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            is StudentServicesError.UserNotFound -> Problem.userNotFound
            is StudentServicesError.CourseNotFound -> Problem.courseNotFound
        }
    }
}

