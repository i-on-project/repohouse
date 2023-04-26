package isel.ps.classcode.presentation.utils

import isel.ps.classcode.domain.ProblemJson

/**
 * Represents the possible errors that can occur when requesting the ClassCode API
 * This will be user on Either.Left
 */

sealed class ClassCodeResponseServicesError {
    class FailDeserialization(val error: String) : GitHubResponseServicesError()
    class FailGitHub(val error: ProblemJson) : GitHubResponseServicesError()
}
