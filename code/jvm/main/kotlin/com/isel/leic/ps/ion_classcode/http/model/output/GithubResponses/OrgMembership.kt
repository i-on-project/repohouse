package com.isel.leic.ps.ion_classcode.http.model.output.GithubResponses

data class OrgMembership(
    val state: String,
    val role: String,
    val organization_url: String,
    val organization: Organization,
    val user: User,
)

data class Organization(
    val login: String,
    val id: Int,
    val node_id: String,
    val url: String,
    val repos_url: String,
    val events_url: String,
    val hooks_url: String,
    val issues_url: String,
    val members_url: String,
    val public_members_url: String,
    val avatar_url: String,
    val description: String,
)

data class User(
    val name: String? = null,
    val email: String? = null,
    val login: String,
    val id: Int,
    val node_id: String,
    val avatar_url: String,
    val gravatar_id: String,
    val url: String,
    val html_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val starred_url: String,
    val subscriptions_url: String,
    val organizations_url: String,
    val repos_url: String,
    val events_url: String,
    val received_events_url: String,
    val type: String,
    val site_admin: Boolean,
)