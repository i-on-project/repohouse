package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.LeaveCourseInputInterface
import com.isel.leic.ps.ion_classcode.domain.requests.LeaveCourse
import com.isel.leic.ps.ion_classcode.repository.request.LeaveCourseRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiLeaveCourseRequestRepository(
    private val handle: Handle,
) : LeaveCourseRepository {

    override fun createLeaveCourseRequest(request: LeaveCourseInputInterface): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO request (creator, composite,state)
            VALUES (:creator, :compositeId, 'Pending')
            RETURNING id
            """,
        )
            .bind("creator", request.creator)
            .bind("compositeId", request.composite)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

        return handle.createUpdate(
            """
            INSERT INTO leavecourse (id, course_id)
            VALUES (:id, :courseId)
            """,
        )
            .bind("id", id)
            .bind("courseId", request.courseId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

    override fun getLeaveCourseRequests(): List<LeaveCourse> {
        return handle.createQuery(
            """
            SELECT l.id, r.creator, r.state, l.course_id, r.composite FROM leavecourse as l JOIN request as r ON l.id = r.id
            """,
        )
            .mapTo<LeaveCourse>()
            .list()
    }

    override fun getLeaveCourseRequestById(id: Int): LeaveCourse? {
        return handle.createQuery(
            """
            SELECT l.id, r.creator, r.state, l.course_id, r.composite FROM leavecourse as l JOIN request as r ON l.id = r.id
            WHERE l.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<LeaveCourse>()
            .firstOrNull()
    }

    override fun getLeaveCourseRequestsByUser(userId: Int): List<LeaveCourse> {
        return handle.createQuery(
            """
            SELECT l.id, r.creator, r.state, l.course_id, r.composite FROM leavecourse as l JOIN request as r ON l.id = r.id
            WHERE r.creator = :creator 
            """,
        )
            .bind("creator", userId)
            .mapTo<LeaveCourse>()
            .list()
    }
}
