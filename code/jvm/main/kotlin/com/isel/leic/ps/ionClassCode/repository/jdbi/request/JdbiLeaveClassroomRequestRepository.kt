package com.isel.leic.ps.ionClassCode.repository.jdbi.request

import com.isel.leic.ps.ionClassCode.domain.input.request.LeaveClassroomInput
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveClassroom
import com.isel.leic.ps.ionClassCode.repository.request.LeaveClassroomRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Leave Classroom Request methods
 */
class JdbiLeaveClassroomRequestRepository(
    private val handle: Handle,
) : LeaveClassroomRepository {

    /**
     * Method to create a Leave Classroom Request
     */
    override fun createLeaveClassroomRequest(request: LeaveClassroomInput, creator: Int): LeaveClassroom {
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
            INSERT INTO leaveClassroom (id, classroom_id)
            VALUES (:id, :classroomId)
            """,
        )
            .bind("id", id)
            .bind("classroomId", request.classroomId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        val githubUsername = handle.createQuery(
            """
            SELECT u.github_username FROM users u WHERE u.id=:id
            """,
        )
            .bind("id", creator)
            .mapTo<String>()
            .first()
        return LeaveClassroom(id, creator, classroomId = request.classroomId, composite = request.composite, githubUsername = githubUsername)
    }

    /**
     * Method to get all Leave Classroom Request's
     */
    override fun getLeaveClassroomRequests(): List<LeaveClassroom> {
        return handle.createQuery(
            """
            SELECT l.id, r.creator, r.state, l.classroom_id, r.composite, (SELECT u.github_username FROM users u WHERE u.id=r.creator) FROM leaveclassroom as l JOIN request as r ON l.id = r.id
            """,
        )
            .mapTo<LeaveClassroom>()
            .list()
    }

    /**
     * Method to get a Leave Classroom Request by is id
     */
    override fun getLeaveClassroomRequestById(id: Int): LeaveClassroom? {
        return handle.createQuery(
            """
            SELECT l.id, r.creator, r.state, l.classroom_id, r.composite, (SELECT u.github_username FROM users u WHERE u.id=r.creator) FROM leaveclassroom as l JOIN request as r ON l.id = r.id
            WHERE l.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<LeaveClassroom>()
            .firstOrNull()
    }

    override fun getLeaveClassroomRequestByCompositeId(composite: Int): List<LeaveClassroom> {
        return handle.createQuery(
            """
            SELECT l.id, r.creator, r.state, l.classroom_id, r.composite, (SELECT u.github_username FROM users u WHERE u.id=r.creator) FROM leaveclassroom as l JOIN request as r ON l.id = r.id
            WHERE r.composite = :composite
            """,
        )
            .bind("composite", composite)
            .mapTo<LeaveClassroom>()
            .list()
    }

    /**
     * Method to get all Leave Classroom Request's by a user
     */
    override fun getLeaveClassroomRequestsByUser(userId: Int): List<LeaveClassroom> {
        return handle.createQuery(
            """
            SELECT l.id, r.creator, r.state, l.classroom_id, r.composite, (SELECT u.github_username FROM users u WHERE u.id=r.creator) FROM leaveclassroom as l JOIN request as r ON l.id = r.id
            WHERE r.creator = :creator 
            """,
        )
            .bind("creator", userId)
            .mapTo<LeaveClassroom>()
            .list()
    }

    override fun getLeaveClassroomRequestsByClassroom(classroomId: Int): List<LeaveClassroom> {
        return handle.createQuery(
            """
            SELECT l.id, r.creator, r.state, l.classroom_id, r.composite, (SELECT u.github_username FROM users u WHERE u.id=r.creator) FROM leaveclassroom as l JOIN request as r ON l.id = r.id
            WHERE l.classroom_id = :classroomId AND r.state != 'Accepted'
            """,
        )
            .bind("classroomId", classroomId)
            .mapTo<LeaveClassroom>()
            .list()
    }
}
