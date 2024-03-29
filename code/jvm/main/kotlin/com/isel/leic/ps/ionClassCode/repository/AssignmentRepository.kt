package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.Assignment
import com.isel.leic.ps.ionClassCode.domain.input.AssignmentInput

/**
 * Repository functions for Assigment Repository
 */
interface AssignmentRepository {
    fun createAssignment(assignment: AssignmentInput): Assignment
    fun getAssignmentById(assignmentId: Int): Assignment?
    fun getClassroomAssignments(classroomId: Int): List<Assignment>
    fun deleteAssignment(assignmentId: Int)
    fun updateAssignmentTitle(assignmentId: Int, title: String)
    fun updateAssignmentDescription(assignmentId: Int, description: String)
    fun updateAssignmentNumbElemsPerGroup(assignmentId: Int, numb: Int)
    fun updateAssignmentNumbGroups(assignmentId: Int, numb: Int)
}
