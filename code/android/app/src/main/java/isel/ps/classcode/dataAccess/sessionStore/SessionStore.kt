package isel.ps.classcode.dataAccess.sessionStore

import kotlinx.coroutines.flow.Flow

/**
 * The contract to the SessionStore. It defines the methods that the SessionStore must implement.
 */
interface SessionStore {
    suspend fun storeGithubToken(token: String)
    suspend fun storeClassCodeSessionCookie(token: String)
    suspend fun storeSecret(secret: String)
    fun getGithubToken(): Flow<String>
    fun getSessionCookie(): Flow<String>

    fun getSecret(): Flow<String>

    suspend fun cleanSecret()
    suspend fun cleanTokens()
    suspend fun checkIfTokensExists(): Boolean
}