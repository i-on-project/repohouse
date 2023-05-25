package isel.ps.classcode.dataAccess.userInfoStore

import isel.ps.classcode.domain.UserInfo
import kotlinx.coroutines.flow.Flow

/**
 * The contract to the UserInfoStore. It defines the methods that the UserInfoStore must implement.
 */
interface UserInfoStore {
    suspend fun storeUserInfo(userInfo: UserInfo)
    fun getUserInfo(): Flow<UserInfo>
}
