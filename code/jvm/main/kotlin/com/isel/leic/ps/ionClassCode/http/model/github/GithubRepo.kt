package com.isel.leic.ps.ionClassCode.http.model.github

import java.sql.Timestamp

/**
 * GitHub Repository model.
 */
data class GithubRepo(
    val id: Int,
    val name: String,
    val full_name: String,
    val owner: Owner,
    val private: Boolean,
    val html_url: String,
    val description: String,
    val fork: Boolean,
    val url: String,
    val forks_url: String,
    val keys_url: String,
    val collaborators_url: String,
    val teams_url: String,
    val hooks_url: String,
    val issue_events_url: String,
    val events_url: String,
    val assignees_url: String,
    val branches_url: String,
    val tags_url: String,
    val blobs_url: String,
    val git_tags_url: String,
    val git_refs_url: String,
    val trees_url: String,
    val statuses_url: String,
    val languages_url: String,
    val stargazers_url: String,
    val contributors_url: String,
    val subscribers_url: String,
    val subscription_url: String,
    val commits_url: String,
    val git_commits_url: String,
    val comments_url: String,
    val issue_comment_url: String,
    val contents_url: String,
    val compare_url: String,
    val merges_url: String,
    val archive_url: String,
    val downloads_url: String,
    val issues_url: String,
    val pulls_url: String,
    val milestones_url: String,
    val notifications_url: String,
    val labels_url: String,
    val releases_url: String,
    val deployments_url: String,
    val created_at: String,
    val updated_at: String,
    val pushed_at: String,
    val git_url: String,
    val ssh_url: String,
    val clone_url: String,
    val svn_url: String,
    val homepage: String,
    val size: Int,
    val stargazers_count: Int,
    val watchers_count: Int,
    val language: String,
    val has_issues: Boolean,
    val has_projects: Boolean,
    val has_downloads: Boolean,
    val has_wiki: Boolean,
    val has_pages: Boolean,
    val forks_count: Int,
    val mirror_url: String,
    val archived: Boolean,
    val open_issues_count: Int,
    val license: License,
    val forks: Int,
    val open_issues: Int,
    val watchers: Int,
    val default_branch: String,
)

/**
 * GitHub Repository Owner model.
 */
data class Owner(
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

/**
 * GitHub Repository License model.
 */
data class License(
    val key: String,
    val name: String,
    val spdx_id: String,
    val url: String,
    val node_id: String,
)

/**
 * GitHub OrganizationRepository model.
 */
data class RepoOrg(
    val name: String,
    val full_name: String,
    val owner: Owner,
    val private: Boolean?,
    val description: String?,
    val commits_url: String,
    val collaborators_url: String,
    val tags_url: String,
)

/**
 * GitHub Organization model.
 */
data class RepoReponse(
    val name: String,
    val collaborators: List<Collaborator>,
    val tags: List<Tag>
)

/**
 * GitHub Collaborator model.
 */
data class Collaborator(
    val login: String,
    val id: Int,
    val permissions: Permissions
)

/**
 * GitHub Tag model.
 */
data class Tag(
    val name: String,
    val date: Timestamp
)

/**
 * GitHub Tags model.
 */
data class Tags(
    val name: String,
    val commit: CommitTag
)

/**
 * GitHub Tag Commit model.
 */
data class CommitTag(
    val sha: String,
    val url: String
)

/**
 * GitHub Commit model.
 */
data class Commit(
    val commit: CommitInfo
)

/**
 * GitHub Commit Info model.
 */
data class CommitInfo(
    val author: Author
)

/**
 * GitHub Author model.
 */
data class Author(
    val name: String,
    val email: String,
    val date: String
)

/**
 * GitHub Orgaznization Role model.
 */
data class GithubOrgRole(
    val role: String
)