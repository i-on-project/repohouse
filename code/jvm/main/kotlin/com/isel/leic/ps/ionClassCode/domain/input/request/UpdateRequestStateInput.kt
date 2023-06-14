package com.isel.leic.ps.ionClassCode.domain.input.request

/**
 * Change Request State Input
 */
data class UpdateRequestStateInput(val type: String, val creator: Int, val requestId: Int, val state: String) {
    fun checkIfTypeNotValid(): Boolean {
        val lowercase = type.lowercase()
        return !(lowercase == "jointeam" || lowercase == "leaveteam")
    }
}

/**
 * Change Request State Input
 */
data class LeaveRequestStateInput(val requestId: Int, val creator: Int)
