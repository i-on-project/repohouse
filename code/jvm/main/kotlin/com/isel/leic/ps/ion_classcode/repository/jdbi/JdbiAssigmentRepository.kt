package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.input.AssigmentInput
import com.isel.leic.ps.ion_classcode.repository.AssigmentRepository
import org.jdbi.v3.core.Handle

class JdbiAssigmentRepository(private val handle: Handle) : AssigmentRepository {
    override fun createAssigment(assigment: AssigmentInput): Int {
        return handle.createUpdate(
            """
               INSERT INTO assignment (title, description, max_number_elems, max_number_groups,classroom_id,release_date) 
                   VALUES (:title, :description, :numb_elems_per_group, :numb_groups,:classroom_id, CURRENT_DATE)
               """,
        )
            .bind("title", assigment.title)
            .bind("description", assigment.description)
            .bind("numb_elems_per_group", assigment.maxNumberElems)
            .bind("numb_groups", assigment.maxNumberElems)
            .bind("classroom_id", assigment.classroomId)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()
    }

    override fun getAssigmentById(assigmentId: Int): Assigment {
        return handle.createQuery(
            """
                SELECT * FROM assignment WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assigmentId).mapTo(Assigment::class.java).one()
    }

    override fun deleteAssigment(assigmentId: Int) {
        handle.createUpdate(
            """
                DELETE FROM assignment WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assigmentId).execute()
    }

    override fun updateAssigmentTitle(assigmentId: Int, title: String) {
        handle.createUpdate(
            """
                UPDATE assignment SET title = :title WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assigmentId).bind("title", title).execute()
    }

    override fun updateAssigmentDescription(assigmentId: Int, description: String) {
        handle.createUpdate(
            """
                UPDATE assignment SET description = :description WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assigmentId).bind("description", description).execute()
    }

    override fun updateAssigmentNumbElemsPerGroup(assigmentId: Int, numb: Int) {
        handle.createUpdate(
            """
                UPDATE assignment SET max_number_elems = :numb WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assigmentId).bind("numb", numb).execute()
    }

    override fun updateAssigmentNumbGroups(assigmentId: Int, numb: Int) {
        handle.createUpdate(
            """
                UPDATE assignment SET max_number_groups = :numb WHERE id = :assigmentId
            """,
        ).bind("assigmentId", assigmentId).bind("numb", numb).execute()
    }
}
