package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.UserInfoDeserialization

data class UserInfo(
    val login: String,
    val id: Long,
    val email: String,
    val name: String,
    val avatarUrl: String,
) {
    constructor(userInfoDeserialization: UserInfoDeserialization) : this(
        login = userInfoDeserialization.login,
        id = userInfoDeserialization.id,
        name = userInfoDeserialization.name,
        avatarUrl = userInfoDeserialization.avatarUrl,
        email = userInfoDeserialization.email,
    )
}
