package com.isel.leic.ps.ion_classcode.domain.requests

interface RequestInterface {
    val id: Int
    val creator: Int
    val state: String
    fun checkState(): Boolean {
        return state == "Pending" || state == "Accepted" || state == "Rejected"
    }
}

data class Request(
    override val id: Int,
    override val creator: Int,
    val composite: Int? = null,
    override val state: String = "Pending",
) : RequestInterface
