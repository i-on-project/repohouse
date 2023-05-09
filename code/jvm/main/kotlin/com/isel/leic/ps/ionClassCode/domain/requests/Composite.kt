package com.isel.leic.ps.ionClassCode.domain.requests

/**
 * Composite Request Interface
 */
data class Composite(
    override val id: Int,
    override val creator: Int,
    override val state: String = "Pending",
    override val composite: Int? = null,
    val requests: List<Int>,
) : RequestInterface
