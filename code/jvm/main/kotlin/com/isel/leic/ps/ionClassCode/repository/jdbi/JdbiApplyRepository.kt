package com.isel.leic.ps.ionClassCode.repository.jdbi

import com.isel.leic.ps.ionClassCode.domain.Apply
import com.isel.leic.ps.ionClassCode.domain.PendingTeacher
import com.isel.leic.ps.ionClassCode.domain.input.ApplyInput
import com.isel.leic.ps.ionClassCode.repository.ApplyRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Apply Request methods
 */
class JdbiApplyRepository(
    private val handle: Handle,
) : ApplyRepository {

    /**
     * Method to create an Apply Request
     */
    override fun createApplyRequest(request: ApplyInput): Apply {
        val id = handle.createUpdate(
            """
            INSERT INTO apply (pending_teacher_id, state) 
            VALUES (:pendingTeacherId, 'Pending')
            RETURNING id
            """,
        )
            .bind("pendingTeacherId", request.pendingTeacherId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        return Apply(id, request.pendingTeacherId, "Pending")
    }

    /**
     * Method to get all Apply Request's
     */
    override fun getApplyRequests(): List<Apply> {
        return handle.createQuery(
            """
            SELECT id, pending_teacher_id,state FROM apply 
            """,
        )
            .mapTo(Apply::class.java)
            .list()
    }

    /**
     * Method to get an Apply Request by is id
     */
    override fun getApplyRequestById(id: Int): Apply? {
        return handle.createQuery(
            """
            SELECT id, pending_teacher_id,state FROM apply 
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo<Apply>()
            .firstOrNull()
    }

    /**
     * Method to get all Apply Request's by a user
     */
    override fun getApplyRequestsByUser(teacherId: Int): List<Apply> {
        return handle.createQuery(
            """
            SELECT id, pending_teacher_id,state FROM apply 
            WHERE pending_teacher_id = :teacherId
            """,
        )
            .bind("teacherId", teacherId)
            .mapTo<Apply>()
            .list()
    }

    override fun getPendingTeacherByApply(applyId: Int): PendingTeacher? {
        return handle.createQuery(
            """
            SELECT pendingteacher.id, email, is_created, github_username, github_id, token, name, github_token, created_at FROM pendingteacher 
            JOIN apply a on pendingteacher.id = a.pending_teacher_id
            WHERE a.id = :applyId
            """,
        )
            .bind("applyId", applyId)
            .mapTo<PendingTeacher>()
            .firstOrNull()
    }

    override fun changeApplyRequestState(id: Int, state: String): Boolean {
        return handle.createUpdate(
            """
            UPDATE apply SET state = :state
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .bind("state", state)
            .execute() > 0
    }
}
