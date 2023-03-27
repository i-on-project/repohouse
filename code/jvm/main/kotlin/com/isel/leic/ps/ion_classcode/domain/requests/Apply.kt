package com.isel.leic.ps.ion_classcode.domain.requests

data class Apply(
    override val id: Int,
    override val creator: Int,
    override val state: String = "pending",
    val compositeId: Int? = null
) : Request {
    init {
        require(checkState()) { "Invalid state" }
    }
}
