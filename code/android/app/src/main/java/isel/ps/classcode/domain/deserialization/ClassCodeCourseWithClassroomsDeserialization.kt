package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeCourseDto = SirenEntity<ClassCodeCourseWithClassroomsDeserialization>
val ClassCodeCourseDtoType = SirenEntity.getType<ClassCodeCourseWithClassroomsDeserialization>()

data class ClassCodeCourseWithClassroomsDeserialization (
    @JsonProperty("id")val id: Int,
    @JsonProperty("orgUrl")val orgUrl: String,
    @JsonProperty("name")val name: String,
    @JsonProperty("teacher")val teacher: List<ClassCodeTeacherDeserialization>,
    @JsonProperty("isArchived")val isArchived: Boolean,
    @JsonProperty("classrooms")val classrooms: List<ClassCodeClassroomDeserialization>,
)
