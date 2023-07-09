package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class used to deserialize the request to leave a classroom response from the database
 */
data class ClassCodeLeaveClassroomRequestDeserialization(
    @JsonProperty("leaveClassroom") val leaveClassroom: ClassCodeLeaveClassroomDeserialization,
    @JsonProperty("leaveTeamRequests") val leaveTeamRequests: List<ClassCodeLeaveTeamWithRepoNameDeserialization>,
)
