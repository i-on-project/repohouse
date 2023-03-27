package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.CreateRepoInput
import com.isel.leic.ps.ion_classcode.domain.requests.CreateRepo
import com.isel.leic.ps.ion_classcode.repository.request.CreateRepoRepository
import org.jdbi.v3.core.Handle

class JdbiCreateRepoRequestRepository(
    private val handle: Handle,
) : CreateRepoRepository {

    override fun createCreateRepoRequest(request: CreateRepoInput): Int {
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
            INSERT INTO createrepo (id, repo_id)
            VALUES (:id, :repoId)
            """,
        )
            .bind("id", id)
            .bind("repoId", request.repoId)
            .execute()
    }

    override fun getCreateRepoRequests(): List<CreateRepo> {
        return handle.createQuery(
            """
            SELECT * FROM createrepo
            """,
        )
            .mapTo(CreateRepo::class.java)
            .list()
    }

    override fun getCreateRepoRequestById(id: Int): CreateRepo {
        return handle.createQuery(
            """
            SELECT * FROM createrepo
            WHERE id = :id
            """,
        )
            .bind("id", id)
            .mapTo(CreateRepo::class.java)
            .first()
    }

    override fun getCreateRepoRequestsByUser(userId: Int): List<CreateRepo> {
        return handle.createQuery(
            """
            SELECT createrepo.id,creator,state,composite FROM createrepo
            JOIN request ON request.id = createrepo.id
            WHERE request.creator = :userId
            """,
        )
            .bind("userId", userId)
            .mapTo(CreateRepo::class.java)
            .list()
    }
}
