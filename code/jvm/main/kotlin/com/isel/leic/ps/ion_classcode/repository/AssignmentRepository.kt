package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Assignment
import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput

interface AssignmentRepository {
    fun createAssignment(assignment: AssignmentInput): Assignment
    fun getAssignmentById(assignmentId: Int): Assignment?
    fun getAssignmentsByClassroom(classroomId: Int): List<Assignment>
    fun deleteAssignment(assignmentId: Int)
    fun updateAssignmentTitle(assignmentId: Int, title: String)
    fun updateAssignmentDescription(assignmentId: Int, description: String)
    fun updateAssignmentNumbElemsPerGroup(assignmentId: Int, numb: Int)
    fun updateAssignmentNumbGroups(assignmentId: Int, numb: Int)
}
