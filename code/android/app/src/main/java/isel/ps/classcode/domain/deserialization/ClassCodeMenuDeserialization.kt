package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeMenuDto = SirenEntity<ClassCodeMenuDeserialization>
val ClassCodeMenuDtoType = SirenEntity.getType<ClassCodeMenuDeserialization>()

/**
 * Class used to deserialize the ClassCodeMenu response from the database
 */

data class ClassCodeMenuDeserialization(
    @JsonProperty("name") val name: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("courses") val courses: List<ClassCodeCourseWithoutClassroomsDeserialization>,
)
