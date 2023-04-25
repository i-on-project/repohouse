package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ion_classcode.domain.requests.CreateRepo
import com.isel.leic.ps.ion_classcode.repository.request.CreateRepoRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Create Repo Request methods
 */
class JdbiCreateRepoRequestRepository(
    private val handle: Handle,
) : CreateRepoRepository {

    /**
     * Method to create a Create Repo Request
     */
    override fun createCreateRepoRequest(request: CreateRepoInput,creator:Int): Int {
        val id = handle.createUpdate(
            """
            INSERT INTO request (creator, composite, state)
            VALUES (:creator, :compositeId, 'Pending')
            RETURNING id
            """,
        )
            .bind("creator", creator)
            .bind("compositeId", request.composite)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

        return handle.createUpdate(
            """
            INSERT INTO createrepo (id, team_id)
            VALUES (:id, :teamId)
            """,
        )
            .bind("id", id)
            .bind("teamId", request.teamId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

    /**
     * Method to gel all a Create Repo Request's
     */
    override fun getCreateRepoRequests(): List<CreateRepo> {
        return handle.createQuery(
            """
            SELECT c.id, c.team_id,r.creator, r.state, r.composite FROM createrepo as c JOIN request as r ON r.id = c.id
            """,
        )
            .mapTo<CreateRepo>()
            .list()
    }

    /**
     * Method to get a Create Repo Request by is id
     */
    override fun getCreateRepoRequestById(id: Int): CreateRepo? {
        return handle.createQuery(
            """
            SELECT createrepo.id, createrepo.team_id,request.creator, request.state, request.composite FROM createrepo 
            JOIN request  ON request.id = createrepo.id
            WHERE createrepo.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<CreateRepo>()
            .firstOrNull()
    }

    /**
     * Method to get all Create Repo Request's by a user
     */
    override fun getCreateRepoRequestsByUser(userId: Int): List<CreateRepo> {
        return handle.createQuery(
            """
            SELECT createrepo.id, createrepo.team_id,creator, state, composite FROM createrepo
            JOIN request ON request.id = createrepo.id
            WHERE request.creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo<CreateRepo>()
            .list()
    }
}
