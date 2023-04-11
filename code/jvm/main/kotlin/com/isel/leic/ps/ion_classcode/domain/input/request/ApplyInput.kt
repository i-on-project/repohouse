package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Apply Request Input Interface
 */
data class ApplyInput(
    override val composite: Int? = null,
    override val creator: Int,
) : RequestInputInterface {
    init {
        require(creator > 0) { "Creator id must be greater than 0" }
        if (composite != null) {
            require(composite > 0) { "Composite id must be greater than 0" }
        }
    }
}
