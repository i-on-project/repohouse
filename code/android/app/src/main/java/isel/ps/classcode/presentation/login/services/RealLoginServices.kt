package isel.ps.classcode.presentation.login.services

import android.app.Activity
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.AUTH_KEY
import isel.ps.classcode.CLASSCODE_LINK_BUILDER
import isel.ps.classcode.MEDIA_TYPE
import isel.ps.classcode.R
import isel.ps.classcode.TAG
import isel.ps.classcode.TOKEN_KEY
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.domain.deserialization.ClassCodeAuthDto
import isel.ps.classcode.domain.deserialization.ClassCodeAuthDtoType
import isel.ps.classcode.http.NavigationRepository
import isel.ps.classcode.http.handleSirenResponseClassCode
import isel.ps.classcode.http.send
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.presentation.bootUp.services.BootUpServices
import isel.ps.classcode.presentation.utils.Either
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * Helper class, just to store the result of the request with the header needed
 */
private data class Storage(val body: Either<HandleClassCodeResponseError, ClassCodeAuthDto>, val sessionCookie: String = "")

/**
 * Implementation of the [LoginServices] interface that will be used for the real app
 */
data class ChallengeInfoInput(val code: String, val state: String, val secret: String)
class RealLoginServices(private val httpClient: OkHttpClient, private val objectMapper: ObjectMapper, private val sessionStore: SessionStore, private val navigationRepo: NavigationRepository, private val bootUpServices: BootUpServices) :
    LoginServices {
    override suspend fun getTheAccessToken(code: String, state: String): Either<HandleClassCodeResponseError, Unit> {
        val secret = sessionStore.getSecret().first()
        sessionStore.cleanSecret()
        val uri = navigationRepo.ensureLink(key = TOKEN_KEY, fetchLink = { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        val requestBody = ChallengeInfoInput(code = code, state = state, secret = secret)
        val request = Request.Builder()
            .url(CLASSCODE_LINK_BUILDER(uri.href))
            .post(objectMapper.writeValueAsString(requestBody).toRequestBody(MEDIA_TYPE))
            .build()
        val result = request.send(httpClient) { response ->
            val headerName = "Set-Cookie"
            val sessionCookie = response.headers[headerName] ?: return@send Storage(body = Either.Left(value = HandleClassCodeResponseError.FailToGetTheHeader(error = "Fail to get the value in header $headerName")))
            val body = handleSirenResponseClassCode<ClassCodeAuthDto>(response = response, type = ClassCodeAuthDtoType, jsonMapper = objectMapper)
            return@send Storage(sessionCookie  = sessionCookie, body = body)
        }
        return when(result.body) {
            is Either.Left -> Either.Left(value = result.body.value)
            is Either.Right -> {
                val authInfo = result.body.value.properties
                sessionStore.storeClassCodeSessionCookie(token = result.sessionCookie)
                sessionStore.storeGithubToken(token = authInfo.accessToken)
                Either.Right(value = Unit)
            }
        }
    }

    override suspend fun startOauth(activity: Activity): Either<HandleClassCodeResponseError, Unit> {
        val secret = generateSecret()
        sessionStore.storeSecret(secret = secret)
        val challenge = generateCodeChallenge(secret = secret)
        val ensureLink = navigationRepo.ensureLink(key = AUTH_KEY, fetchLink =  { bootUpServices.getHome() }) ?: return Either.Left(value = HandleClassCodeResponseError.LinkNotFound())
        return try {
            val uri = Uri.parse(CLASSCODE_LINK_BUILDER(ensureLink.href)).buildUpon().apply {
                appendQueryParameter("challenge", challenge)
                appendQueryParameter("challengeMethod", "s256")
            }.build()
            val customTabsIntent = CustomTabsIntent.Builder().build()
            //customTabsIntent.intent.setPackage(null)
            customTabsIntent.intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            customTabsIntent.launchUrl(activity, uri)
            Either.Right(value = Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open URL", e)
            Toast
                .makeText(
                    activity,
                    R.string.failed_url_open,
                    Toast.LENGTH_LONG
                )
                .show()
            Either.Left(value = HandleClassCodeResponseError.Fail(error = e.message ?: "Failed to open URL"))
        }
    }

    private fun generateSecret(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun generateCodeChallenge(secret: String): String {
        val bytes = secret.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }
}
