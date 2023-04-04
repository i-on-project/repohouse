package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput

interface AssigmentRepository {
    fun createAssignment(assignment: AssignmentInput): Assigment
    fun getAssignmentById(assignmentId: Int): Assigment?
    fun getAssignmentsByClassroom(classroomId: Int): List<Assigment>
    fun deleteAssignment(assignmentId: Int)
    fun updateAssignmentTitle(assignmentId: Int, title: String)
    fun updateAssignmentDescription(assignmentId: Int, description: String)
    fun updateAssignmentNumbElemsPerGroup(assignmentId: Int, numb: Int)
    fun updateAssignmentNumbGroups(assignmentId: Int, numb: Int)
}
