package isel.ps.classcode.dataAccess.sessionStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import isel.ps.classcode.dataAccess.CryptoManager
import isel.ps.classcode.dataAccess.DecryptionSecret
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * The implementation of session store that will be used in the final product.
 * It uses the preferences data store to store the encrypted token and the iv(used to decrypt the token),
 * and the [CryptoManager] to encrypt and decrypt the token.
 */
class RealSessionStore(private val cryptoManager: CryptoManager, context: Context): SessionStore {
    companion object {
        val githubTokenKey = stringPreferencesKey("github_token")
        val classCodeTokenKey = stringPreferencesKey("classCode_token")
        val githubIv = stringPreferencesKey("github_iv")
        val classCodeIv = stringPreferencesKey("classCode_iv")
    }
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sessionManager")
    private val dataStore = context.dataStore

    override suspend fun storeGithubToken(token: String) {
        val encryptedToken = cryptoManager.encrypt(data = token)
        dataStore.edit { preferences ->
            preferences[githubTokenKey] = encryptedToken.data
            preferences[githubIv] = encryptedToken.iv
        }
    }

    override suspend fun storeClassCodeToken(token: String) {
        val encryptedToken = cryptoManager.encrypt(data = token)
        dataStore.edit { preferences ->
            preferences[classCodeTokenKey] = encryptedToken.data
            preferences[classCodeIv] = encryptedToken.iv
        }
    }

    override fun getGithubToken(): Flow<String> =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val iv = preferences[githubIv] ?: ""
            val encryptedData = preferences[githubTokenKey] ?: ""
            cryptoManager.decrypt(decryptionSecret = DecryptionSecret(
                iv = iv,
                data = encryptedData
            )
            ).data
        }

    override fun getClassCodeToken(): Flow<String> =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val iv = preferences[classCodeIv] ?: ""
            val encryptedData = preferences[classCodeTokenKey] ?: ""
            cryptoManager.decrypt(decryptionSecret = DecryptionSecret(
                iv = iv,
                data = encryptedData
            )
            ).data
        }

    override suspend fun checkIfTokenExists(): Boolean {
        return dataStore.data.map { preferences ->
            preferences.contains(githubTokenKey) // Use a default value if the entry doesn't exist
        }.first()
    }
}