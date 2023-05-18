package isel.ps.classcode

import android.app.Application
import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.dataAccess.CryptoManager
import isel.ps.classcode.dataAccess.sessionStore.FakeSessionStore
import isel.ps.classcode.dataAccess.sessionStore.RealSessionStore
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import isel.ps.classcode.http.NavigationRepository
import isel.ps.classcode.presentation.bootUp.services.BootUpServices
import isel.ps.classcode.presentation.bootUp.services.FakeBootUpServices
import isel.ps.classcode.presentation.bootUp.services.RealBootUpServices
import isel.ps.classcode.presentation.classroom.services.ClassroomServices
import isel.ps.classcode.presentation.classroom.services.FakeClassroomServices
import isel.ps.classcode.presentation.classroom.services.RealClassroomServices
import isel.ps.classcode.presentation.course.services.CourseServices
import isel.ps.classcode.presentation.course.services.FakeCourseServices
import isel.ps.classcode.presentation.course.services.RealCourseServices
import isel.ps.classcode.presentation.login.services.FakeLoginServices
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.login.services.RealLoginServices
import isel.ps.classcode.presentation.menu.services.FakeMenuServices
import isel.ps.classcode.presentation.menu.services.MenuServices
import isel.ps.classcode.presentation.menu.services.RealMenuServices
import okhttp3.OkHttpClient

const val TAG = "Classcode"
const val iS_REAL_IMPLEMENTATION = true

/**
 * The implementations of the various modules that are referenced in [DependenciesContainer].
 */
data class Dependencies(
    override val sessionStore: SessionStore,
    override val loginServices: LoginServices,
    override val menuServices: MenuServices,
    override val courseServices: CourseServices,
    override val classroomServices: ClassroomServices,
    override val bootUpServices: BootUpServices
) : DependenciesContainer

class ClassCodeApplication : DependenciesContainer, Application() {
    private val httpClient: OkHttpClient by lazy { OkHttpClient() }
    private val objectMapper: ObjectMapper by lazy { ObjectMapper() }
    private val cryptoManager: CryptoManager by lazy { CryptoManager() }
    private val navigationRepository: NavigationRepository by lazy { NavigationRepository() }
    private val dependencies: DependenciesContainer by lazy { dependencies(isRealImplementation = iS_REAL_IMPLEMENTATION, context = this, cryptoManager = cryptoManager, objectMapper = objectMapper, httpClient = httpClient, navigationRepo = navigationRepository) }
    override val sessionStore: SessionStore by lazy { dependencies.sessionStore }
    override val bootUpServices: BootUpServices by lazy { dependencies.bootUpServices }
    override val loginServices: LoginServices by lazy { dependencies.loginServices }
    override val menuServices: MenuServices by lazy { dependencies.menuServices }
    override val courseServices: CourseServices by lazy { dependencies.courseServices }
    override val classroomServices: ClassroomServices by lazy { dependencies.classroomServices }
}

/**
 * The various dependencies of the project
 */
interface DependenciesContainer {
    val sessionStore: SessionStore
    val bootUpServices: BootUpServices
    val loginServices: LoginServices
    val menuServices: MenuServices
    val courseServices: CourseServices
    val classroomServices: ClassroomServices
}

private fun dependencies(isRealImplementation: Boolean = false, context: Context, cryptoManager: CryptoManager, objectMapper: ObjectMapper, httpClient: OkHttpClient, navigationRepo: NavigationRepository): Dependencies {
    val sessionStore = if (isRealImplementation) RealSessionStore(context = context, cryptoManager = cryptoManager) else FakeSessionStore(alreadyLoggedIn = true)
    return if (isRealImplementation) {
        val bootUpServices = RealBootUpServices(
            httpClient = httpClient,
            objectMapper = objectMapper,
            navigationRepo = navigationRepo
        )
        Dependencies(
            sessionStore = sessionStore,
            bootUpServices = bootUpServices,
            loginServices = RealLoginServices(
                httpClient = httpClient,
                objectMapper = objectMapper,
                sessionStore = sessionStore,
                navigationRepo = navigationRepo,
                bootUpServices = bootUpServices
            ),
            menuServices = RealMenuServices(
                httpClient = httpClient,
                objectMapper = objectMapper,
                sessionStore = sessionStore,
                navigationRepo = navigationRepo,
                bootUpServices = bootUpServices
            ),
            courseServices = RealCourseServices(
                httpClient = httpClient,
                objectMapper = objectMapper,
                sessionStore = sessionStore,
                navigationRepo = navigationRepo,
                bootUpServices = bootUpServices
            ),
            classroomServices = RealClassroomServices(
                httpClient = httpClient,
                objectMapper = objectMapper,
                sessionStore = sessionStore,
                navigationRepo = navigationRepo,
                bootUpServices = bootUpServices
            )
        )
    } else {
        Dependencies(
            sessionStore = sessionStore,
            loginServices = FakeLoginServices(),
            bootUpServices = FakeBootUpServices(),
            menuServices = FakeMenuServices(),
            courseServices = FakeCourseServices(),
            classroomServices = FakeClassroomServices()
        )
    }
}