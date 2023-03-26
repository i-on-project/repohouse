package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.domain.input.TeamInput

interface ClassroomRepository {
    fun createClassroom(classroom: ClassroomInput): Int
    fun deleteClassroom(classroomId: Int)
    fun enterClassroom(classroomId: Int, userId: Int)
    fun leaveClassroom(classroomId: Int, userId: Int)
    fun getClassroomById(classroomId: Int): Classroom?
    fun getTeamsOfAClassroom(classroomId: Int): List<TeamInput>
}

