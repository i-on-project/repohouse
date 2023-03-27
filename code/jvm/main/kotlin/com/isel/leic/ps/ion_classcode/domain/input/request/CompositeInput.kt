package com.isel.leic.ps.ion_classcode.domain.input.request

data class CompositeInput(
    val requests: List<Int>,
    override val creator: Int,
    override val composite: Int? = null
) : RequestInput {
    init {
        require(requests.isNotEmpty()) { "Requests must not be empty" }
    }
}
