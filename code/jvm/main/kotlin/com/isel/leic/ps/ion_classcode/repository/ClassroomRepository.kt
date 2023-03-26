package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Classroom
import com.isel.leic.ps.ion_classcode.domain.input.AssigmentInput
import com.isel.leic.ps.ion_classcode.domain.input.ClassroomInput
import com.isel.leic.ps.ion_classcode.domain.input.TeamInput

interface ClassroomRepository {
    fun createClassroom(classroom: ClassroomInput): Int
    fun deleteClassroom(classroomId: Int)
    fun getClassroomById(classroomId: Int): Classroom?
    fun getAssigmentsOfAClassroom(classroomId: Int): List<Assigment>
}

