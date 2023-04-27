package isel.ps.classcode.domain

/**
 * Represents a error response body from ClassCode api
 */
data class ProblemJson (
    val type: String,
    val title: String,
    val detail: String,
)
