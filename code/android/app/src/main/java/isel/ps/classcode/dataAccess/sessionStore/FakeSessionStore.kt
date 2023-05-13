package isel.ps.classcode.dataAccess.sessionStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 *  Implementation of session store for tests
 */
class FakeSessionStore(alreadyLoggedIn: Boolean): SessionStore {
    private var gitHubTokenStorage: String? = if (alreadyLoggedIn) "githubToken" else null
    private var classCodeTokenStorage: String? = if (alreadyLoggedIn) "classcodeToken" else null
    override suspend fun storeGithubToken(token: String) {
        gitHubTokenStorage = token
    }

    override suspend fun storeClassCodeSessionCookie(token: String) {
        classCodeTokenStorage = token
    }

    override suspend fun storeSecret(secret: String) {
        TODO("Not yet implemented")
    }

    override fun getGithubToken(): Flow<String> {
        val token = gitHubTokenStorage ?: ""
        return flowOf(token)
    }

    override fun getSessionCookie(): Flow<String> {
        val token = classCodeTokenStorage ?: ""
        return flowOf(token)
    }

    override fun getSecret(): Flow<String> {
        TODO("Not yet implemented")
    }

    override suspend fun cleanSecret() {
        TODO("Not yet implemented")
    }

    override suspend fun cleanTokens() {
        gitHubTokenStorage = null
        classCodeTokenStorage = null
    }

    override suspend fun checkIfTokensExists(): Boolean =
        gitHubTokenStorage != null && classCodeTokenStorage != null
}
