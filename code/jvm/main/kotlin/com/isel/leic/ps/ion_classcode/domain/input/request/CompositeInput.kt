package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Composite Request Input Interface
 */
data class CompositeInput(
    val requests: List<Int>,
    override val creator: Int,
    override val composite: Int? = null
) : RequestInputInterface {
    init {
        require(requests.isNotEmpty()) { "Requests must not be empty" }
    }
}
