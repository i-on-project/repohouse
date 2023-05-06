package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

data class ClassCodeCourseWithoutClassroomsDeserialization (
    @JsonProperty("id") val id: Int,
    @JsonProperty("orgUrl")val orgUrl: String,
    @JsonProperty("name")val name: String,
    @JsonProperty("orgId")val orgId: Long,
    @JsonProperty("teacher")val teacher: List<ClassCodeTeacherDeserialization>
)