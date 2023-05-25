package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ProblemJsonDeserialization

/**
 * Represents a error response body from ClassCode api
 */
data class ProblemJson(
    val type: String,
    val title: String,
    val detail: String,
) {
    constructor(problemJsonDeserialization: ProblemJsonDeserialization) : this(
        type = problemJsonDeserialization.type,
        title = problemJsonDeserialization.title,
        detail = problemJsonDeserialization.detail,
    )
}
