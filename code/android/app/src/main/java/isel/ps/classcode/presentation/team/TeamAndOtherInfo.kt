package isel.ps.classcode.presentation.team

import isel.ps.classcode.domain.Team

data class TeamAndOtherInfo(val team: Team, val courseName: String, val courseId: Int, val classroomId: Int)