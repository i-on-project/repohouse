package isel.ps.classcode.dataAccess.sessionStore

import kotlinx.coroutines.flow.Flow

/**
 * The contract to the SessionStore. It defines the methods that the SessionStore must implement.
 */
interface SessionStore {
    suspend fun storeGithubToken(token: String)
    fun getGithubToken(): Flow<String>
    suspend fun checkIfTokenExists(): Boolean
}