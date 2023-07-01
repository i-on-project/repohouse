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
    override fun createArchiveRepoRequest(request: ArchiveRepoInput, creator: Int): ArchiveRepo {
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
        val name = handle.createQuery(
            """
            SELECT name FROM repo WHERE id = :repoId
            """,
        )
            .bind("repoId", request.repoId)
            .mapTo<String>()
            .first()
        return ArchiveRepo(id = id, creator = creator, state = "Pending", composite = request.composite, repoId = request.repoId, repoName = name)
    }

    /**
     * Method to get all Archive Repo Request's
     */
    override fun getArchiveRepoRequests(): List<ArchiveRepo> {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite, x.repo_id, x.name AS repo_name FROM 
            (SELECT a.id, a.repo_id, r.name FROM archiverepo a JOIN repo r on a.repo_id = r.id) as x
            JOIN request r on r.id = x.id
            """,
        )
            .mapTo<ArchiveRepo>()
            .list()
    }

    override fun getArchiveRepoRequestForClassroom(classroomId: Int): List<ArchiveRepo> {
        return handle.createQuery(
            """
                SELECT r.id, r.creator, r.state, r.composite, x.repo_id, x.name AS repo_name FROM 
                (SELECT a.id FROM assignment a JOIN classroom c on a.classroom_id = c.id WHERE c.id = :classroomId) as y
                JOIN team t on t.assignment = y.id
                JOIN (SELECT a.id, a.repo_id, r.name, r.team_id FROM archiverepo a JOIN repo r on a.repo_id = r.id) as x on x.team_id = t.id
                JOIN request r on r.id = x.id
                WHERE r.state != 'Accepted'
            """
        )
            .bind("classroomId", classroomId)
            .mapTo<ArchiveRepo>()
            .list()
    }

    /**
     * Method to get an Archive Repo Request by is id
     */
    override fun getArchiveRepoRequestById(id: Int): ArchiveRepo? {
        return handle.createQuery(
            """
            SELECT r.id, r.creator, r.state, r.composite, x.repo_id, x.name AS repo_name FROM 
            (SELECT a.id, a.repo_id, r.name FROM archiverepo a JOIN repo r on a.repo_id = r.id WHERE a.id = :id) as x 
            JOIN request r on r.id = x.id
            """,
        )
            .bind("id", id)
            .mapTo<ArchiveRepo>()
            .firstOrNull()
    }

    override fun getArchiveRepoRequestsByTeam(teamId: Int): ArchiveRepo? {
        return handle.createQuery(
            """
            SELECT x.id, creator, state, composite, x.repo_id, repo.name AS repo_name FROM 
            (SELECT a.id, a.repo_id FROM repo AS r JOIN archiverepo AS a on a.repo_id=r.id WHERE r.team_id = :teamId) as x
            JOIN repo ON repo.id = x.repo_id
            JOIN request ON request.id = x.id
            """,
        )
            .bind("teamId", teamId)
            .mapTo<ArchiveRepo>()
            .firstOrNull()
    }
}
