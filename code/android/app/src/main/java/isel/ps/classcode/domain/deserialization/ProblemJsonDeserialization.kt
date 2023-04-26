package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

data class ProblemJsonDeserialization (
    @JsonProperty("type") val type: String,
    @JsonProperty("title") val title: String,
    @JsonProperty("detail") val detail: String,
)