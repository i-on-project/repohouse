package isel.ps.classcode.dataAccess.userInfoStore

import isel.ps.classcode.domain.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserInfoStore {
    suspend fun storeUserInfo(userInfo: UserInfo)
    fun getUserInfo(): Flow<UserInfo>
}