package com.isel.leic.ps.ion_classcode.domain.input.request

interface RequestInputInterface {
    val creator: Int
    val composite: Int?
}
data class RequestInput(
    override val creator: Int,
    override val composite: Int? = null,
) : RequestInputInterface
