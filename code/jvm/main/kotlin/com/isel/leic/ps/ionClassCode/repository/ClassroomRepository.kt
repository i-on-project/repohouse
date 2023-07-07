package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.Assignment
import com.isel.leic.ps.ionClassCode.domain.Classroom
import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.Team
import com.isel.leic.ps.ionClassCode.domain.input.ClassroomInput
import com.isel.leic.ps.ionClassCode.http.model.input.ClassroomUpdateInputModel

/**
 * Repository functions for Classroom Repository
 */
interface ClassroomRepository {
    fun createClassroom(classroom: ClassroomInput, inviteCode: String): Classroom
    fun updateClassroomName(classroomId: Int, classroomUpdate: ClassroomUpdateInputModel)
    fun deleteClassroom(classroomId: Int)
    fun getClassroomById(classroomId: Int): Classroom?
    fun getAssignmentsOfAClassroom(classroomId: Int): List<Assignment>
    fun archiveClassroom(classroomId: Int)
    fun getStudentClassroomId(courseId: Int, studentId: Int): Int?
    fun getClassroomInviteCode(classroomId: Int): String?
    fun getClassroomByCode(inviteCode: String): Classroom?
    fun getStudentsByClassroom(classroomId: Int): List<Student>
    fun addStudentToClassroom(classroomId: Int, studentId: Int)
    fun getAllInviteLinks(): List<String>
    fun getAllCourseClassrooms(courseId: Int): List<Classroom>
    fun getAllReposInClassroom(classroomId: Int): List<Int>
    fun getAllStudentTeamsInClassroom(classroomId: Int, studentId: Int): List<Team>
    fun leaveClassroom(classroomId: Int, studentId: Int)
}
