package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeMenuDto = SirenEntity<ClassCodeMenuDeserialization>
val ClassCodeMenuDtoType = SirenEntity.getType<ClassCodeMenuDeserialization>()

data class ClassCodeMenuDeserialization (
    @JsonProperty("name") val name: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("courses") val courses: List<ClassCodeCourseWithoutClassroomsDeserialization>,
)
