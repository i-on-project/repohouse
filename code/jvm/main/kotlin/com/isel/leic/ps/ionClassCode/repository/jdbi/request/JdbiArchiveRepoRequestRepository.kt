package com.isel.leic.ps.ionClassCode.repository.jdbi.request

import com.isel.leic.ps.ionClassCode.domain.input.request.ArchiveRepoInput
import com.isel.leic.ps.ionClassCode.domain.requests.ArchiveRepo
import com.isel.leic.ps.ionClassCode.repository.request.ArchiveRepoRequestRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Archive Repo Request methods
 */
class JdbiArchiveRepoRequestRepository(
    private val handle: Handle,
) : ArchiveRepoRequestRepository {

    /**
     * Method to create an Archive Repo Request
     */
    override fun createArchiveRepoRequest(request: ArchiveRepoInput,creator:Int): ArchiveRepo {
        val requestId = handle.createUpdate(
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

        val id = handle.createUpdate(
            """
           INSERT INTO archiverepo (id, repo_id)
           VALUES (:id, :repo_id)
           RETURNING id
           """,
        )
            .bind("id", requestId)
            .bind("repo_id", request.repoId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()

        return ArchiveRepo(id, creator, "Pending", request.composite, request.repoId)
    }

    /**
     * Method to get all Archive Repo Request's
     */
    override fun getArchiveRepoRequests(): List<ArchiveRepo> {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, a.repo_id, r.composite FROM archiverepo as a JOIN request r on r.id = a.id
            """,
        )
            .mapTo(ArchiveRepo::class.java)
            .list()
    }

    /**
     * Method to get an Archive Repo Request by is id
     */
    override fun getArchiveRepoRequestById(id: Int): ArchiveRepo? {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, a.repo_id, r.composite FROM archiverepo as a JOIN request r on r.id = a.id
            WHERE a.id = :id
            """,
        )
            .bind("id", id)
            .mapTo<ArchiveRepo>()
            .firstOrNull()
    }

    /**
     * Method to get all Archive Repo Request's by a user
     */
    override fun getArchiveRepoRequestsByUser(userId: Int): List<ArchiveRepo> {
        return handle.createQuery(
            """
            SELECT archiverepo.id, creator, state, repo_id, composite FROM archiverepo
            JOIN request ON request.id = archiverepo.id
            WHERE request.creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo<ArchiveRepo>()
            .list()
    }
}
