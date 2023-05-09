package isel.ps.classcode.http.utils

import isel.ps.classcode.domain.GitHubError

/**
 * Represents the possible errors that can occur when requesting the GitHub API
 */
sealed class HandleGitHubResponseError {
    class FailDeserialize(val error: String) : HandleGitHubResponseError()
    class FailRequest(val error: GitHubError) : HandleGitHubResponseError()
}