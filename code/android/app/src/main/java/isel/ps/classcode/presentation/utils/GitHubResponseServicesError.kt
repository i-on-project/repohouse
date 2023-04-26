package isel.ps.classcode.presentation.utils

import isel.ps.classcode.domain.GitHubError

sealed class GitHubResponseServicesError {
    class FailDeserialization(val error: String) : GitHubResponseServicesError()
    class FailGitHub(val error: GitHubError) : GitHubResponseServicesError()
}