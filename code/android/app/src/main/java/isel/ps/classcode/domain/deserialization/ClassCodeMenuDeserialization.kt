package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeMenuDto = SirenEntity<ClassCodeMenuDeserialization>
val ClassCodeMenuDtoType = SirenEntity.getType<ClassCodeMenuDeserialization>()

data class ClassCodeMenuDeserialization (
    @JsonProperty("name") val name: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("courses") val courses: List<ClassCodeCoursesDeserialization>,
)

data class ClassCodeCoursesDeserialization (
    @JsonProperty("id") val id: Int,
    @JsonProperty("orgUrl")val orgUrl: String,
    @JsonProperty("name")val name: String,
    @JsonProperty("orgId")val orgId: Long,
    @JsonProperty("teacher")val teacher: List<TeacherDeserialization>
)

data class TeacherDeserialization (
    @JsonProperty("name") val name: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("id") val id: Int,
    @JsonProperty("githubUsername") val githubUsername: String,
    @JsonProperty("githubId") val githubId: Long,
    @JsonProperty("isCreated") val isCreated: Boolean
)