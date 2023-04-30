package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Assignment
import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.http.model.input.ClassroomUpdateInputModel

/**
 * Repository functions for Classroom Repository
 */
interface ClassroomRepository {
    fun createClassroom(classroom: ClassroomInput, inviteLink: String): Classroom?
    fun updateClassroomName(classroomId: Int, classroomUpdate: ClassroomUpdateInputModel)
    fun enterClassroom(classroomId: Int, studentId: Int): Classroom
    fun deleteClassroom(classroomId: Int)
    fun getClassroomById(classroomId: Int): Classroom?
    fun getAssignmentsOfAClassroom(classroomId: Int): List<Assignment>
    fun archiveClassroom(classroomId: Int)
    fun getStudentClassroomId(courseId: Int, studentId: Int): Int?
    fun getClassroomInviteLink(classroomId: Int): String?
    fun getClassroomByInviteLink(inviteLink: String): Classroom?
    fun getStudentsByClassroom(classroomId: Int): List<Student>
    fun addStudentToClassroom(classroomId: Int, studentId: Int)
    fun getAllInviteLinks(): List<String>
    fun getAllCourseClassrooms(courseId: Int): List<Classroom>
}
