package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeAuthDto = SirenEntity<ClassCodeAuthDeserialization>
val ClassCodeAuthDtoType = SirenEntity.getType<ClassCodeAuthDeserialization>()

/**
 * Class used to deserialize the ClassCodeAuth response from the database
 */
data class ClassCodeAuthDeserialization (
    @JsonProperty("accessToken") val accessToken: String,
)