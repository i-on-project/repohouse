package com.isel.leic.ps.ion_classcode.domain.requests

data class Apply(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    val compositeId: Int? = null
) : RequestInterface {
    init {
        require(checkState()) { "Invalid state" }
    }
}
