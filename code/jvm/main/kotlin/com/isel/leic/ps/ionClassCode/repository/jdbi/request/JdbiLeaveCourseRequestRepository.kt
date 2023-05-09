package com.isel.leic.ps.ionClassCode.repository.jdbi.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveCourseInput
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveCourse
import com.isel.leic.ps.ionClassCode.repository.request.LeaveCourseRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Leave Course Request methods
 */
class JdbiLeaveCourseRequestRepository(
    private val handle: Handle,
) : LeaveCourseRepository {

    /**
     * Method to create a Leave Course Request
     */
    override fun createLeaveCourseRequest(request: LeaveCourseInput, creator:Int): LeaveCourse {
        val id = handle.createUpdate(
            """
            INSERT INTO request (creator, composite,state)
            VALUES (:creator, :compositeId, 'Pending')
            RETURNING id
            """,
        )
            .bind("creator", creator)
            .bind("compositeId", request.composite)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

        handle.createUpdate(
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
        return LeaveCourse(id, creator, courseId = request.courseId)
    }

    /**
     * Method to get all Leave Course Request's
     */
    override fun getLeaveCourseRequests(): List<LeaveCourse> {
        return handle.createQuery(
            """
            SELECT l.id, r.creator, r.state, l.course_id, r.composite FROM leavecourse as l JOIN request as r ON l.id = r.id
            """,
        )
            .mapTo<LeaveCourse>()
            .list()
    }

    /**
     * Method to get a Leave Course Request by is id
     */
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

    /**
     * Method to get all Leave Course Request's by a user
     */
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
