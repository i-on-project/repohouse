package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.GithubErrorDeserialization

/**
 * Represents a error response body from GitHub
 */
data class GitHubError(val message: String) {
    constructor(githubErrorDeserialization: GithubErrorDeserialization): this (
        message = githubErrorDeserialization.message
    )
}