package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the error response from github.
 */
data class GithubErrorDeserialization(
    @JsonProperty("message") val message: String,
    @JsonProperty("documentation_url") val documentationUrl: String
)