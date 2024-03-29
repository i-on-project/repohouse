package com.isel.leic.ps.ionClassCode.http.model.github

/**
 * Represents a GitHub Team Created.
 */
data class TeamCreated(
    val id: Int,
    val node_id: String,
    val url: String,
    val html_url: String,
    val name: String,
    val slug: String,
    val description: String? = null,
    val privacy: String,
    val permission: String,
    val members_url: String,
    val repositories_url: String,
    val parent: Parent,
    val members_count: Int,
    val repos_count: Int,
    val created_at: String,
    val updated_at: String,
    val organization: Organization,
    val ldap_dn: String? = null,
)
