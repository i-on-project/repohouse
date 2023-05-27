package com.isel.leic.ps.ionClassCode.repository

import com.isel.leic.ps.ionClassCode.domain.Repo
import com.isel.leic.ps.ionClassCode.domain.input.RepoInput

/**
 * Repository functions for Repo Repository
 */
interface RepoRepository {
    fun createRepo(repo: RepoInput): Repo
    fun deleteRepo(repoId: Int)
    fun updateRepoStatus(repoId: Int, url: String)
    fun getRepoById(repoId: Int): Repo?
    fun getRepoByTeam(teamId: Int): Repo?
    fun archiveRepo(repoId: Int)
}
