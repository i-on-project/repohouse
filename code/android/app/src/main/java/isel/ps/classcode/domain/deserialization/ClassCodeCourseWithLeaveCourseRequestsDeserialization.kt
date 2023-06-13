package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeCourseWithLeaveCourseRequestsDto = SirenEntity<ClassCodeCourseWithLeaveCourseRequestsDeserialization>
val ClassCodeCourseWithLeaveCourseRequestsDtoType = SirenEntity.getType<ClassCodeCourseWithLeaveCourseRequestsDeserialization>()

/**
 * Class used to deserialize the request to leave a team
 */
data class ClassCodeCourseWithLeaveCourseRequestsDeserialization(
    @JsonProperty("course")val course: ClassCodeCourseWithClassroomsDeserialization,
    @JsonProperty("leaveCourseRequests")val leaveCourseRequests: List<ClassCodeLeaveCourseRequestDeserialization> = emptyList()
)