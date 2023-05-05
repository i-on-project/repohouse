package isel.ps.classcode.dataAccess.sessionStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeSessionStore(alreadyLoggedIn: Boolean): SessionStore {
    private var gitHubTokenStorage: String? = if (alreadyLoggedIn) "githubToken" else null
    private var classCodeTokenStorage: String? = if (alreadyLoggedIn) "classcodeToken" else null
    override suspend fun storeGithubToken(token: String) {
        gitHubTokenStorage = token
    }

    override suspend fun storeClassCodeSessionCookie(token: String) {
        classCodeTokenStorage = token
    }

    override fun getGithubToken(): Flow<String> {
        val token = gitHubTokenStorage ?: ""
        return flowOf(token)
    }

    override fun getClassCodeToken(): Flow<String> {
        val token = classCodeTokenStorage ?: ""
        return flowOf(token)
    }

    override suspend fun checkIfGithubTokenExists(): Boolean {
        return gitHubTokenStorage != null
    }

    override suspend fun checkIfClassCodeTokenExists(): Boolean {
        return classCodeTokenStorage != null
    }
}
