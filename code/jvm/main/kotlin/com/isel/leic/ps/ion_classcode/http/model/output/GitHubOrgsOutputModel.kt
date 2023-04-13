package com.isel.leic.ps.ion_classcode.http.model.output

/**
 * Represents a GitHubOrgs Model.
 */
data class GitHubOrgsModel(
    val login: String,
    val url :String,
    val avatar_url: String
) : OutputModel



/**
 * Represents a GitHubOrgs Output Model.
 */
data class GitHubOrgsOutputModel(
    val orgs : List<GitHubOrgsModel>
) : OutputModel

