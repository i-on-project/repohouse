package com.isel.leic.ps.ion_classcode.repository.jdbi.request

import com.isel.leic.ps.ion_classcode.domain.input.request.*
import com.isel.leic.ps.ion_classcode.domain.requests.Apply
import com.isel.leic.ps.ion_classcode.domain.requests.ArchieveRepo
import com.isel.leic.ps.ion_classcode.repository.request.RequestRepository
import com.isel.leic.ps.ion_classcode.domain.requests.Request
import com.isel.leic.ps.ion_classcode.repository.request.ApplyRequestRepository
import com.isel.leic.ps.ion_classcode.repository.request.ArchieveRepoRequestRepository
import org.jdbi.v3.core.Handle

class JdbiArchieveRepoRequestRepository(
    private val handle: Handle,
) : ArchieveRepoRequestRepository {

   override fun createArchieveRepoRequest(request: ArchieveRepoInput): Int {

       val requestId = handle.createUpdate(
           """
           INSERT INTO request (creator, composite,state)
           VALUES (:creator, :compositeId,'pending')
           RETURNING id
           """
       )
           .bind("creator", request.creator)
           .bind("composite", request.composite)
           .execute()

       return handle.createUpdate(
           """
           INSERT INTO archiverepo (id,repo_id)
           VALUES (:creator, :repo_id)
           RETURNING id
           """
       )
           .bind("id", requestId)
           .bind("repo_id", request.repoId)
           .execute()

   }

    override fun getArchieveRepoRequests(): List<ArchieveRepo> {
        return handle.createQuery(
            """
            SELECT * FROM archiverepo
            """
        )
            .mapTo(ArchieveRepo::class.java)
            .list()
    }

    override fun getArchieveRepoRequestById(id: Int): ArchieveRepo {
        return handle.createQuery(
            """
            SELECT * FROM archiverepo
            WHERE id = :id
            """
        )
            .bind("id", id)
            .mapTo(ArchieveRepo::class.java)
            .first()
    }

    override fun getArchieveRepoRequestsByUser(userId: Int): List<ArchieveRepo> {
        return handle.createQuery(
            """
            SELECT archiverepo.id,creator,state,repo_id,composite FROM archiverepo
            JOIN request ON request.id = archiverepo.id
            WHERE request.creator = :userId
            """
        )
            .bind("userId", userId)
            .mapTo(ArchieveRepo::class.java)
            .list()
    }
}
