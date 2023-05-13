package com.isel.leic.ps.ionClassCode.domain.input.request

/**
 * Composite Request Input Interface
 */
data class CompositeInput(
    val requests: List<Int>,
    override val composite: Int? = null
) : RequestInputInterface {
    init {
        require(requests.isNotEmpty()) { "Requests must not be empty" }
    }
}