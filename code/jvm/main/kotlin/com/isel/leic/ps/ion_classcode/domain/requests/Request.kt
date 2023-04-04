package com.isel.leic.ps.ion_classcode.domain.requests

interface Request {
    val id: Int
    val creator: Int
    val state: String
    val composite: Int?

    fun checkState(): Boolean {
        return state == "Pending" || state == "Accepted" || state == "Rejected"
    }
}
