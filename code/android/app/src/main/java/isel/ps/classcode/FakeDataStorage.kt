package isel.ps.classcode

import isel.ps.classcode.domain.ArchiveRepo
import isel.ps.classcode.domain.Assignment
import isel.ps.classcode.domain.Course
import isel.ps.classcode.domain.CreateTeamComposite
import isel.ps.classcode.domain.Team

class FakeDataStorage() {
    private val assignments: MutableList<Assignment> = mutableListOf()
    private val archiveRepo: MutableList<ArchiveRepo>? = null
    private val teamsCreated: MutableList<Team> = mutableListOf()
    private val courses: MutableList<Course> = mutableListOf()
    private val createTeamComposite: MutableList<CreateTeamComposite> = mutableListOf()

    fun getCourses(): List<Course> = courses
    fun getAssignments(classroomId: Int, courseId: Int): List<Assignment> = assignments.filter { it.classroomId == classroomId }
    fun getArchiveRepo(): List<ArchiveRepo>? = archiveRepo
}
