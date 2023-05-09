package isel.ps.classcode.presentation.menu.services

import isel.ps.classcode.domain.Course
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.http.utils.HandleClassCodeResponseError
import isel.ps.classcode.http.utils.HandleGitHubResponseError
import isel.ps.classcode.presentation.login.services.LoginServices
import isel.ps.classcode.presentation.utils.Either

/**
 * Implementation of the [MenuServices] interface that will be used for tests
 */
class FakeMenuServices : MenuServices {
    override suspend fun getUserInfo(): Either<HandleGitHubResponseError, UserInfo> {
        return Either.Right(value = UserInfo(login = "PeterGriffin23", id = 123L, name = "Peter Griffin", avatarUrl = "https://avatars.githubusercontent.com/u/29706842?v=4"))
    }

    override suspend fun getCourses(): Either<HandleClassCodeResponseError, List<Course>> {
        return Either.Right(value = listOf(Course(id = 1, name = "PDM", orgId = 6817318, orgUrl = "https://avatars.githubusercontent.com/u/6817318?s=200&v=4"), Course(id = 2, name = "LAE", orgId = 5501606, orgUrl = "https://avatars.githubusercontent.com/u/5501606?s=200&v=4"), Course(id = 3, name = "DAW", orgId = 10852760, orgUrl = "https://avatars.githubusercontent.com/u/10852760?s=200&v=4")))
    }

    override suspend fun logout() {
        // Do nothing
    }
}