package com.isel.leic.ps.ionClassCode.repository.jdbi.request

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
        return CreateRepo(id = id, repoId = request.repoId, creator = creator, repoName = request.repoName, composite = request.composite)
    }

    /**
     * Method to gel all a Create Repo Request's
     */
    override fun getCreateRepoRequests(): List<CreateRepo> {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite, x.repo_id, x.name as repo_name FROM
            (SELECT c.id, c.repo_id, r.name FROM createrepo c JOIN repo r on r.id = c.repo_id) AS x 
            JOIN request as r ON r.id = x.id
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
            SELECT r.id, r.creator, r.state, r.composite, x.repo_id, x.name as repo_name FROM 
            (SELECT c.id, c.repo_id, r.name FROM createrepo c JOIN repo r on r.id = c.repo_id) AS x 
            JOIN request r ON r.id = x.id
             WHERE r.id = :id
            """
        )
            .bind("id", id)
            .mapTo<CreateRepo>()
            .firstOrNull()
    }

    override fun getCreateRepoRequestByCompositeId(compositeId: Int): CreateRepo? {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite, x.repo_id, x.name  as repo_name FROM (
                SELECT c.id, c.repo_id, r.name FROM createrepo as c JOIN repo r on c.repo_id = r.id
            ) AS x JOIN request r on x.id = r.id
            WHERE composite = :compositeId
                        """,
        )
            .bind("compositeId", compositeId)
            .mapTo<CreateRepo>()
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
