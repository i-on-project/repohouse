package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the response from the ClassCode api
 */
data class ProblemJsonDeserialization (
    @JsonProperty("type") val type: String,
    @JsonProperty("title") val title: String,
    @JsonProperty("detail") val detail: String,
)
