package isel.ps.classcode.dataAccess.sessionStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import isel.ps.classcode.dataAccess.userInfoStore.UserInfoStore
import isel.ps.classcode.domain.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class RealUserInfoStore(context: Context): UserInfoStore {
    companion object {
        val login = stringPreferencesKey("login")
        val id = longPreferencesKey("id")
        val email = stringPreferencesKey("email")
        val name = stringPreferencesKey("name")
        val avatarUrl = stringPreferencesKey("avatar_url")
    }
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sessionManager")
    private val dataStore = context.dataStore

    override suspend fun storeUserInfo(userInfo: UserInfo) {
        dataStore.edit { preferences ->
            preferences[login] = userInfo.login
            preferences[id] = userInfo.id
            preferences[email] = userInfo.email
            preferences[name] = userInfo.name
            preferences[avatarUrl] = userInfo.avatarUrl
        }

    }

    override fun getUserInfo(): Flow<UserInfo> =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val login = preferences[login] ?: ""
            val id = preferences[id] ?: -1L
            val email = preferences[email] ?: ""
            val name = preferences[name] ?: ""
            val avatarUrl = preferences[avatarUrl] ?: ""
            UserInfo(login = login, id = id, email = email, name = name, avatarUrl = avatarUrl)
        }
}