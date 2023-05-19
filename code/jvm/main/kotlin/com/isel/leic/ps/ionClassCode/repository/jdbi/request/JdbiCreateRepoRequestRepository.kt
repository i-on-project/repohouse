package com.isel.leic.ps.ionClassCode.repository.jdbi.request

import com.isel.leic.ps.ionClassCode.domain.RepoNotCreated
import com.isel.leic.ps.ionClassCode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ionClassCode.domain.requests.CreateRepo
import com.isel.leic.ps.ionClassCode.repository.request.CreateRepoRepository
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
    override fun createCreateRepoRequest(request: CreateRepoInput, creator: Int): CreateRepo {
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

        handle.createUpdate(
            """
            INSERT INTO createrepo (id, repo_id)
            VALUES (:id, :repo_id)
            """,
        )
            .bind("id", id)
            .bind("repo_id", request.repoId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        return CreateRepo(id, request.repoId, creator)
    }

    /**
     * Method to gel all a Create Repo Request's
     */
    override fun getCreateRepoRequests(): List<CreateRepo> {
        return handle.createQuery(
            """
            SELECT c.id, c.repo_id,r.creator, r.state, r.composite FROM createrepo as c JOIN request as r ON r.id = c.id
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
            SELECT createrepo.id, createrepo.repo_id,request.creator, request.state, request.composite FROM createrepo 
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
            SELECT createrepo.id, createrepo.repo_id, creator, state, composite FROM createrepo
            JOIN request ON request.id = createrepo.id
            WHERE request.creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo<CreateRepo>()
            .list()
    }

    override fun getCreateRepoRequestByCompositeId(compositeId: Int): RepoNotCreated? {
        return handle.createQuery(
            """
            SELECT x.repo_id, x.name, x.id, r.creator, r.state, r.composite FROM (
                SELECT c.id, c.repo_id, r2.name FROM createrepo as c JOIN repo r2 on c.repo_id = r2.id
            ) AS x JOIN request r on x.id = r.id
            WHERE composite = :compositeId
                        """,
        )
            .bind("compositeId", compositeId)
            .mapTo<RepoNotCreated>()
            .firstOrNull()
    }

    override fun updateCreateRepoState(requestId: Int, state: String) {
        handle.createUpdate(
            """
            UPDATE request
            SET state = :state
            WHERE id = :requestId
            """,
        )
            .bind("requestId", requestId)
            .bind("state", state)
            .execute()
    }
}
