package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthInfoDeserialization(@JsonProperty("access_token") val accessToken: String, @JsonProperty("token_type") val tokenType: String, @JsonProperty("scope") val scope: String)