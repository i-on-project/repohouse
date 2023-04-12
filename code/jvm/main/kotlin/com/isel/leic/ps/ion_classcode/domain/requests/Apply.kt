package com.isel.leic.ps.ion_classcode.domain.requests

/**
 * Apply Request Interface
 */
data class Apply(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    override val composite: Int? = null
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
