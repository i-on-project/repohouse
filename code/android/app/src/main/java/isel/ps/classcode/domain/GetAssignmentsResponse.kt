package isel.ps.classcode.domain

/**
 * Represents the response of the get assignments request
 */
data class GetAssignmentsResponse(val assignments: List<Assignment>, val archiveRepos: List<ArchiveRepo>?, val leaveClassroomsRequests: List<LeaveClassroomRequest>)
