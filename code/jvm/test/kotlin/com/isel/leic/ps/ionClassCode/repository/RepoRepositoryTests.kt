package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.input.RepoInput
import com.isel.leic.ps.ionClassCode.repository.jdbi.JdbiRepoRepository
import com.isel.leic.ps.ionClassCode.utils.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class RepoRepositoryTests {

    @Test
    fun `can create a repo`() = testWithHandleAndRollback { handle ->
        val repoRepo = JdbiRepoRepository(handle = handle)
        val teamId = 3
        val created = repoRepo.createRepo(repo = RepoInput(name = "name", url = "status", teamId = teamId))
        val repo = repoRepo.getRepoById(repoId = created.id)
        assert(repo != null)
    }

    @Test
    fun `can get a repo by id`() = testWithHandleAndRollback { handle ->
        val repoRepo = JdbiRepoRepository(handle = handle)
        val repoId = 1
        val name = "repo1"
        val repo = repoRepo.getRepoById(repoId = repoId) ?: fail("Repo not found")
        assert(repo.name == name)
    }

    @Test
    fun `can update repo status`() = testWithHandleAndRollback { handle ->
        val repoRepo = JdbiRepoRepository(handle = handle)
        val repoId = 1
        repoRepo.updateRepoStatus(repoId = repoId, url = "url")
        val repo = repoRepo.getRepoById(repoId = repoId) ?: fail("Repo not found")
        assert(repo.isCreated)
    }

    @Test
    fun `can get repo by teamId`() = testWithHandleAndRollback { handle ->
        val repoRepo = JdbiRepoRepository(handle = handle)
        val teamId = 1
        val repo = repoRepo.getRepoByTeam(teamId = teamId)
        assert(repo != null)
    }

    @Test
    fun `can delete a repo`() = testWithHandleAndRollback { handle ->
        val repoRepo = JdbiRepoRepository(handle = handle)
        val repoId = 4
        repoRepo.deleteRepo(repoId = repoId)
        val repo = repoRepo.getRepoById(repoId = repoId)
        assert(repo == null)
    }

    @Test
    fun `cannot delete a repo`() = testWithHandleAndRollback { handle ->
        val repoRepo = JdbiRepoRepository(handle = handle)
        val repoId = 1
        try {
            repoRepo.deleteRepo(repoId = repoId)
            fail("Should not be able to delete a repo with deliveries")
        } catch (e: Exception) {
            assert(true)
        }
    }
}
