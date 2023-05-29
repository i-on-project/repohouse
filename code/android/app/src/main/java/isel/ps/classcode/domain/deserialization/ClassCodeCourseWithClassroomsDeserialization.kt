package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the ClassCodeCourse response from the database
 */
data class ClassCodeCourseWithClassroomsDeserialization(
    @JsonProperty("id")val id: Int,
    @JsonProperty("orgUrl")val orgUrl: String,
    @JsonProperty("name")val name: String,
    @JsonProperty("teacher")val teacher: List<ClassCodeTeacherDeserialization>,
    @JsonProperty("isArchived")val isArchived: Boolean,
    @JsonProperty("classrooms")val classrooms: List<ClassCodeClassroomDeserialization>,
)
