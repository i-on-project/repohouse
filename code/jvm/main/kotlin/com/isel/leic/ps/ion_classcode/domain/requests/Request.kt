package com.isel.leic.ps.ion_classcode.domain.requests

interface Request {
    val id: Int
    val creator: Int
    val state: String

    fun checkState(): Boolean {
        return state == "pending" || state == "accepted" || state == "rejected"
    }
}
