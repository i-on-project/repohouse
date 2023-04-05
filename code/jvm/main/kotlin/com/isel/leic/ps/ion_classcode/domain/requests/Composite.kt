package com.isel.leic.ps.ion_classcode.domain.requests

data class Composite(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    val requests: List<Int>,
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
