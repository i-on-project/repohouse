package com.isel.leic.ps.ionClassCode.repository.jdbi

import com.isel.leic.ps.ionClassCode.domain.Assignment
import com.isel.leic.ps.ionClassCode.domain.input.AssignmentInput
import com.isel.leic.ps.ionClassCode.repository.AssignmentRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp

/**
 * Implementation of the Assigment methods
 */
class JdbiAssignmentRepository(private val handle: Handle) : AssignmentRepository {

    /**
     * Method to create an Assigment
     */
    override fun createAssignment(assignment: AssignmentInput): Assignment {
        val id = handle.createUpdate(
            """
               INSERT INTO assignment (title, description, max_elems_per_group, max_number_groups,classroom_id, release_date) 
               VALUES (:title, :description, :max_elems_per_group, :numb_groups,:classroom_id, CURRENT_DATE)
               RETURNING id
               """,
        )
            .bind("title", assignment.title)
            .bind("description", assignment.description)
            .bind("max_elems_per_group", assignment.maxElemsPerGroup)
            .bind("numb_groups", assignment.maxNumberGroups)
            .bind("classroom_id", assignment.classroomId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        return Assignment(id, assignment.classroomId, assignment.minElemsPerGroup,assignment.maxElemsPerGroup, assignment.maxNumberGroups, Timestamp(System.currentTimeMillis()), assignment.description, assignment.title)
    }

    /**
     * Method to get an Assigment by is id
     */
    override fun getAssignmentById(assignmentId: Int): Assignment? {
        return handle.createQuery(
            """
                SELECT * FROM assignment WHERE id = :assigmentId
            """,
        )
            .bind("assigmentId", assignmentId)
            .mapTo<Assignment>()
            .firstOrNull()
    }

    /**
     * Method to get an Assigment by a classroom
     */
    override fun getClassroomAssignments(classroomId: Int): List<Assignment> {
        return handle.createQuery(
            """
                SELECT * FROM assignment WHERE classroom_id = :classroomId
            """,
        )
            .bind("classroomId", classroomId)
            .mapTo<Assignment>()
            .list()
    }

    /**
     * Method to delete an Assigment
     */
    override fun deleteAssignment(assignmentId: Int) {
        handle.createUpdate(
            """
                DELETE FROM assignment WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assignmentId).execute()
    }

    /**
     * Method to update an Assigment title
     */
    override fun updateAssignmentTitle(assignmentId: Int, title: String) {
        handle.createUpdate(
            """
                UPDATE assignment SET title = :title WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assignmentId).bind("title", title).execute()
    }

    /**
     * Method to update an Assigment description
     */
    override fun updateAssignmentDescription(assignmentId: Int, description: String) {
        handle.createUpdate(
            """
                UPDATE assignment SET description = :description WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assignmentId).bind("description", description).execute()
    }

    /**
     * Method to update an Assigment number of elements per group
     */
    override fun updateAssignmentNumbElemsPerGroup(assignmentId: Int, numb: Int) {
        handle.createUpdate(
            """
                UPDATE assignment SET max_elems_per_group = :numb WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assignmentId).bind("numb", numb).execute()
    }

    /**
     * Method to update an Assigment number of groups
     */
    override fun updateAssignmentNumbGroups(assignmentId: Int, numb: Int) {
        handle.createUpdate(
            """
                UPDATE assignment SET max_number_groups = :numb WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assignmentId).bind("numb", numb).execute()
    }
}
