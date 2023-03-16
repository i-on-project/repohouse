package com.isel.leic.ps.ion_repohouse.http.model.output.gitHubResponses

data class TeamList(
    val id: Int,
    val node_id: String,
    val url: String,
    val name: String,
    val slug: String,
    val description: String? = null,
    val privacy: String,
    val permission: String,
    val permissions: Permissions,
    val html_url: String,
    val members_url: String,
    val repositories_url: String,
    val parent: Parent
)

data class Permissions(
    val pull: Boolean,
    val triage: Boolean,
    val push: Boolean,
    val maintain: Boolean,
    val admin: Boolean,
)

data class Parent(
    val id: String,
    val node_id: String,
    val url: String,
    val members_url: String,
    val name: String,
    val description: String? = null,
    val permission: String,
    val privacy: String,
    val html_url: String,
    val repositories_url: String,
    val slug: String,
    val ldap_dn: String,

)