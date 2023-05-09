package com.isel.leic.ps.ionClassCode.http.model.github

/**
 * Represents a GitHub Organization Repository Created.
 */
data class OrgRepoCreated(
    val id: Int,
    val node_id: String,
    val name: String,
    val full_name: String,
    val owner: Owner,
    val private: Boolean,
    val html_url: String,
    val description: String? = null,
    val fork: Boolean,
    val url: String,
    val archive_url: String,
    val assignees_url: String,
    val blobs_url: String,
    val branches_url: String,
    val collaborators_url: String,
    val comments_url: String,
    val commits_url: String,
    val compare_url: String,
    val contents_url: String,
    val contributors_url: String,
    val deployments_url: String,
    val downloads_url: String,
    val events_url: String,
    val forks_url: String,
    val git_commits_url: String,
    val git_refs_url: String,
    val git_tags_url: String,
    val git_url: String,
    val issue_comment_url: String,
    val issue_events_url: String,
    val issues_url: String,
    val keys_url: String,
    val labels_url: String,
    val languages_url: String,
    val merges_url: String,
    val milestones_url: String,
    val notifications_url: String,
    val pulls_url: String,
    val releases_url: String,
    val ssh_url: String,
    val stargazers_url: String,
    val statuses_url: String,
    val subscribers_url: String,
    val subscription_url: String,
    val tags_url: String,
    val teams_url: String,
    val trees_url: String,
    val clone_url: String,
    val mirror_url: String? = null,
    val hooks_url: String,
    val svn_url: String,
    val homepage: String? = null,
    val language: String? = null,
    val forks_count: Int,
    val stargazers_count: Int,
    val watchers_count: Int,
    val size: Int,
    val default_branch: String,
    val open_issues_count: Int,
    val is_template: Boolean,
    val topics: List<String>,
    val has_issues: Boolean,
    val has_projects: Boolean,
    val has_wiki: Boolean,
    val has_pages: Boolean,
    val has_downloads: Boolean,
    val hasDiscussions: Boolean,
    val archived: Boolean,
    val disabled: Boolean,
    val visibility: String,
    val pushed_at: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val permissions: Permissions,
    val role_name: String,
    val temp_clone_token: String,
    val delete_branch_on_merge: Boolean,
    val subscribers_count: Int,
    val network_count: Int,
    val code_of_conduct: CodeOfConduct? = null,
    val license: License,
    val forks: Int,
    val open_issues: Int,
    val watchers: Int,
    val allow_forking: Boolean,
    val web_commit_sigoff_required: Boolean,
    val security_and_analysis: SecurityAndAnalysis,
)

/**
 * Represents a GitHub Organization Repository Created Owner.
 */
data class CodeOfConduct(
    val key: String,
    val name: String,
    val url: String,
    val html_url: String? = null,
    val body: String
)

/**
 * Represents a Security and Analysis GitHub model.
 */
data class SecurityAndAnalysis(
    val advanced_security: AdvancedSecurity,
    val secret_scanning: SecretScanning,
    val secret_scanning_push_protection: SecretScanningPushProtection,
)

/**
 * Represents a Secret Scanning Push Protection GitHub model.
 */
data class SecretScanningPushProtection(
    val enabled: String
)

/**
 * Represents a Secret Scanning GitHub model.
 */
data class SecretScanning(
    val enabled: String
)

/**
 * Represents a Advanced Security GitHub model.
 */
data class AdvancedSecurity(
    val enabled: String
)
