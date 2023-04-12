package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Request Input Interface
 */
interface RequestInputInterface {
    val creator: Int
    val composite: Int?
}
data class RequestInput(
    override val creator: Int,
    override val composite: Int? = null,
) : RequestInputInterface {
    init {
        require(creator > 0) { "Creator must be greater than 0" }
        if (composite != null) {
            require(composite > 0) { "Composite must be greater than 0" }
        }
    }
}
