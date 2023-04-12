package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.Repo
import com.isel.leic.ps.ion_classcode.domain.input.RepoInput
import com.isel.leic.ps.ion_classcode.repository.RepoRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Repo methods
 */
class JdbiRepoRepository(private val handle: Handle) : RepoRepository {
    /**
     * Method to create a Repo
     */
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

    /**
     * Method to delete a Repo
     */
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

    /**
     * Method to update a Repo status
     */
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

    /**
     * Method to get a Repo by is id
     */
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

    /**
     * Method to get all Repos from a team
     */
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
