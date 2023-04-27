package isel.ps.classcode

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.dataAccess.CryptoManager
import isel.ps.classcode.dataAccess.sessionStore.RealSessionStore
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.login.services.RealGithubLoginServices
import isel.ps.classcode.presentation.menu.services.MenuServices
import isel.ps.classcode.presentation.menu.services.RealMenuServices
import okhttp3.OkHttpClient

const val TAG = "Classcode"

private const val API_HOME =  "https://b135-95-95-191-117.eu.ngrok.io"

/**
 * The implementations of the various modules that are referenced in [DependenciesContainer].
 */


class ClassCodeApplication : DependenciesContainer, Application() {
    private val httpClient: OkHttpClient by lazy { OkHttpClient() }
    private val objectMapper: ObjectMapper by lazy { ObjectMapper() }
    private val cryptoManager: CryptoManager by lazy { CryptoManager() }
    override val sessionStore: SessionStore by lazy { RealSessionStore(context = this, cryptoManager = cryptoManager) }
    override val loginServices: LoginServices by lazy { RealGithubLoginServices(httpClient = httpClient, objectMapper = objectMapper, sessionStore = sessionStore) }
    override val menuServices: MenuServices by lazy { RealMenuServices(httpClient = httpClient, objectMapper = objectMapper, sessionStore = sessionStore) }
}

/**
 * The various dependencies of the project
 */
interface DependenciesContainer {
    val sessionStore: SessionStore
    val loginServices: LoginServices
    val menuServices: MenuServices
}