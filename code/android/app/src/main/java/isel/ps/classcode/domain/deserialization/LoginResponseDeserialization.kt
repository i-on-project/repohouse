package isel.ps.classcode.domain.deserialization

/**
 * Class used to deserialize the LoginResponse response from the database
 */
data class ClientTokenDeserialization(
    val accessToken: String,
    val scope: String,
    val tokenType: String
)

/**
 * Class used to deserialize the LoginResponse response from the database
 */
data class LoginResponseDeserialization(
    val tokenInfo: ClientTokenDeserialization,
)