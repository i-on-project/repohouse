package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Repo
import com.isel.leic.ps.ion_classcode.domain.input.RepoInput

/**
 * Repository functions for Repo Repository
 */
interface RepoRepository {
    fun createRepo(repo: RepoInput): Repo
    fun deleteRepo(repoId: Int)
    fun updateRepoStatus(repoId: Int, status: Boolean)
    fun getRepoById(repoId: Int): Repo?
    fun getReposByTeam(teamId: Int): List<Repo>
}
