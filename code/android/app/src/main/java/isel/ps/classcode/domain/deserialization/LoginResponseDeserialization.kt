package isel.ps.classcode.domain.deserialization


data class ClientTokenDeserialization(
    val accessToken: String,
    val scope: String,
    val tokenType: String
)

data class LoginResponseDeserialization(
    val tokenInfo: ClientTokenDeserialization,
)