package com.isel.leic.ps.ion_classcode.repository

import com.isel.leic.ps.ion_classcode.domain.Repo
import com.isel.leic.ps.ion_classcode.domain.input.RepoInput

interface RepoRepository {
    fun createRepo(repo: RepoInput): Int
    fun deleteRepo(repoId: Int)
    fun updateRepoStatus(repoId: Int, status: String)
    fun getRepoById(repoId: Int): RepoInput
    fun getReposByTeam(teamId: Int): List<Repo>
}
