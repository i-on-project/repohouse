package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the ClassCodeCourseWithoutClassroomsDeserialization response from the database
 */

data class ClassCodeCourseWithoutClassroomsDeserialization(
    @JsonProperty("id") val id: Int,
    @JsonProperty("orgUrl")val orgUrl: String,
    @JsonProperty("name")val name: String,
    @JsonProperty("orgId")val orgId: Long,
    @JsonProperty("teacher")val teacher: List<ClassCodeTeacherDeserialization>,
)
