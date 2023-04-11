package com.isel.leic.ps.ion_classcode.domain.requests

/**
 * Composite Request Interface
 */
data class Composite(
    override val id: Int,
    override val creator: Int,
    override val state: String = "pending",
    override val composite: Int? = null,
    val requests: List<Int>,
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
