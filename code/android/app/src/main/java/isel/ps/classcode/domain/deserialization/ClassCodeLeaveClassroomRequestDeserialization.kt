package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty

data class ClassCodeLeaveClassroomRequestDeserialization(
    @JsonProperty("leaveClassroom") val leaveClassroom: ClassCodeLeaveClassroomDeserialization,
    @JsonProperty("leaveTeamRequests") val leaveTeamRequests: List<ClassCodeLeaveTeamWithRepoNameDeserialization>,
)
