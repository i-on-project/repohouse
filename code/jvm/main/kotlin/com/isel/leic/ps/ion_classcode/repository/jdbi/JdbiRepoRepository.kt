package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Repo
import com.isel.leic.ps.ion_classcode.domain.input.RepoInput
import com.isel.leic.ps.ion_classcode.repository.RepoRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiRepoRepository(private val handle: Handle) : RepoRepository {
    override fun createRepo(repo: RepoInput): Int {
        return handle.createUpdate(
            """
                INSERT INTO REPO (name,url,team_id,is_created) 
                VALUES (:name, :url, :teamId, false)
                RETURNING id
                """,
        )
            .bind("name", repo.name)
            .bind("url", repo.url)
            .bind("teamId", repo.teamId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
    }

    override fun deleteRepo(repoId: Int) {
        handle.createUpdate(
            """
                DELETE FROM REPO
                WHERE id = :repoId
                """,
        )
            .bind("repoId", repoId)
            .execute()
    }

    override fun updateRepoStatus(repoId: Int, status: Boolean) {
        handle.createUpdate(
            """
                UPDATE REPO
                SET is_created = :status
                WHERE id = :repoId
                """,
        )
            .bind("repoId", repoId)
            .bind("status", status)
            .execute()
    }

    override fun getRepoById(repoId: Int): Repo? {
        return handle.createQuery(
            """
                SELECT * FROM REPO
                WHERE id = :repoId
                """,
        )
            .bind("repoId", repoId)
            .mapTo<Repo>()
            .firstOrNull()
    }

    override fun getReposByTeam(teamId: Int): List<Repo> {
        return handle.createQuery(
            """
                SELECT * FROM REPO
                WHERE team_id = :teamId
                """,
        )
            .bind("teamId", teamId)
            .mapTo<Repo>()
            .list()
    }
}
