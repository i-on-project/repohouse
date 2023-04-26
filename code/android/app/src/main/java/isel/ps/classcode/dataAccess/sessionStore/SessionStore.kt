package isel.ps.classcode.dataAccess.sessionStore

import kotlinx.coroutines.flow.Flow


interface SessionStore {
    suspend fun storeGithubToken(token: String)
    fun getGithubToken(): Flow<String>
    suspend fun checkIfTokenExists(): Boolean
}