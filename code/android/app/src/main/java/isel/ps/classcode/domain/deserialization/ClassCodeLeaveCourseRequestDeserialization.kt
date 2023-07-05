package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the request to leave a course
 */
data class ClassCodeLeaveCourseRequestDeserialization(
    @JsonProperty("leaveCourse") val leaveCourse: ClassCodeLeaveCourseDeserialization,
    @JsonProperty("leaveClassRoomRequests") val leaveClassroomRequests: List<ClassCodeLeaveClassroomRequestDeserialization>,
)
