package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.input.AssignmentInput
import com.isel.leic.ps.ion_classcode.repository.AssigmentRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.sql.Timestamp

class JdbiAssignmentRepository(private val handle: Handle) : AssigmentRepository {
    override fun createAssignment(assignment: AssignmentInput): Assigment {
        val id = handle.createUpdate(
            """
               INSERT INTO assignment (title, description, max_number_elems, max_number_groups,classroom_id,release_date) 
               VALUES (:title, :description, :numb_elems_per_group, :numb_groups,:classroom_id, CURRENT_DATE)
               RETURNING id
               """,
        )
            .bind("title", assignment.title)
            .bind("description", assignment.description)
            .bind("numb_elems_per_group", assignment.maxNumberElems)
            .bind("numb_groups", assignment.maxNumberElems)
            .bind("classroom_id", assignment.classroomId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        return Assigment(id, assignment.classroomId, assignment.maxNumberElems, assignment.maxNumberGroups, Timestamp(System.currentTimeMillis()), assignment.description, assignment.title)
    }

    override fun getAssignmentById(assignmentId: Int): Assigment? {
        return handle.createQuery(
            """
                SELECT * FROM assignment WHERE id = :assigmentId
            """,
        )
            .bind("assigmentId", assignmentId)
            .mapTo<Assigment>()
            .firstOrNull()
    }

    override fun deleteAssignment(assignmentId: Int) {
        handle.createUpdate(
            """
                DELETE FROM assignment WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assignmentId).execute()
    }

    override fun updateAssignmentTitle(assignmentId: Int, title: String) {
        handle.createUpdate(
            """
                UPDATE assignment SET title = :title WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assignmentId).bind("title", title).execute()
    }

    override fun updateAssignmentDescription(assignmentId: Int, description: String) {
        handle.createUpdate(
            """
                UPDATE assignment SET description = :description WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assignmentId).bind("description", description).execute()
    }

    override fun updateAssignmentNumbElemsPerGroup(assignmentId: Int, numb: Int) {
        handle.createUpdate(
            """
                UPDATE assignment SET max_number_elems = :numb WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assignmentId).bind("numb", numb).execute()
    }

    override fun updateAssignmentNumbGroups(assignmentId: Int, numb: Int) {
        handle.createUpdate(
            """
                UPDATE assignment SET max_number_groups = :numb WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assignmentId).bind("numb", numb).execute()
    }
}
