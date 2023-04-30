package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeAuthDto = SirenEntity<ClassCodeAuthDeserialization>

data class ClassCodeAuthDeserialization (
    @JsonProperty("message") val message: String,
    @JsonProperty("url") val url: String
)