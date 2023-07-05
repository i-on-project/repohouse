package isel.ps.classcode.domain

data class GetAssignmentsResponse(val assignments: List<Assignment>, val archiveRepos: List<ArchiveRepo>?, val leaveClassroomsRequests: List<LeaveClassroomRequest>)
