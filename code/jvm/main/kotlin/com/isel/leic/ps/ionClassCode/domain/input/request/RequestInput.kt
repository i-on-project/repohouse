package com.isel.leic.ps.ionClassCode.domain.input.request

/**
 * Request Input Interface
 */
interface RequestInputInterface {
    val composite: Int?
}
data class RequestInput(
    override val composite: Int? = null,
) : RequestInputInterface {
    init {
        if (composite != null) {
            require(composite > 0) { "Composite must be greater than 0" }
        }
    }
}
