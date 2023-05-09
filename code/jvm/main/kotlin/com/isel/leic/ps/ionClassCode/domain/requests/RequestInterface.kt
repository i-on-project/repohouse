package com.isel.leic.ps.ionClassCode.domain.requests

/**
 * Request Interface
 */
interface RequestInterface {
    val id: Int
    val creator: Int
    val state: String
    val composite: Int?

    fun checkState(): Boolean {
        return state == "Pending" || state == "Accepted" || state == "Rejected"
    }
}

/**
 * Request Data Class
 */
data class Request(
    override val id: Int,
    override val creator: Int,
    override val composite: Int? = null,
    override val state: String = "Pending",
) : RequestInterface
