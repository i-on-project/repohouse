package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.LeaveCourseInput
import com.isel.leic.ps.ion_classcode.domain.requests.LeaveCourse
import com.isel.leic.ps.ion_classcode.repository.request.LeaveCourseRepository
import org.jdbi.v3.core.Handle

class JdbiLeaveCourseRequestRepository(
    private val handle: Handle,
) : LeaveCourseRepository {

    override fun createLeaveCourseRequest(request: LeaveCourseInput): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO request (creator, composite,state)
            VALUES (:creator, :compositeId,'pending')
            RETURNING id
            """,
        )
            .bind("creator", request.creator)
            .bind("composite", request.composite)
            .execute()

        return handle.createUpdate(
            """
            INSERT INTO leavecourse (id, course_id)
            VALUES (:id, :courseId)
            """,
        )
            .bind("id", id)
            .bind("courseId", request.courseId)
            .execute()
    }

    override fun getLeaveCourseRequests(): List<LeaveCourse> {
        return handle.createQuery(
            """
            SELECT * FROM leavecourse
            """,
        )
            .mapTo(LeaveCourse::class.java)
            .list()
    }

    override fun getLeaveCourseRequestById(id: Int): LeaveCourse {
        return handle.createQuery(
            """
            SELECT * FROM leavecourse
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(LeaveCourse::class.java)
            .first()
    }

    override fun getLeaveCourseRequestsByUser(userId: Int): List<LeaveCourse> {
        return handle.createQuery(
            """
            SELECT * FROM leavecourse
            WHERE id IN (
                SELECT id FROM request
                WHERE creator = :creator
            )
            """,
        )
            .bind("creator", userId)
            .mapTo(LeaveCourse::class.java)
            .list()
    }
}
