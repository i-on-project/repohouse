package com.isel.leic.ps.ionClassCode.http.model.output

/**
 * Represents a GitHubOrgs Model.
 */
data class GitHubOrgsModel(
    val login: String,
    val url :String,
    val avatar_url: String,
    val id: Int
) : OutputModel



/**
 * Represents a GitHubOrgs Output Model.
 */
data class GitHubOrgsOutputModel(
    val orgs : List<GitHubOrgsModel>
) : OutputModel

