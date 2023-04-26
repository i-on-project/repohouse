package isel.ps.classcode.presentation.utils

import isel.ps.classcode.domain.ProblemJson

sealed class ClassCodeResponseServicesError {
    class FailDeserialization(val error: String) : GitHubResponseServicesError()
    class FailGitHub(val error: ProblemJson) : GitHubResponseServicesError()
}
