package isel.ps.classcode.presentation.utils

import isel.ps.classcode.domain.GitHubError

/**
 * Represents the possible errors that can occur when requesting the GitHub API
 */

sealed class GitHubResponseServicesError {
    class FailDeserialization(val error: String) : GitHubResponseServicesError()
    class FailGitHub(val error: GitHubError) : GitHubResponseServicesError()
}