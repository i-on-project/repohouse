package com.isel.leic.ps.ionClassCode.domain

/**
 * Apply Request Interface
 */
data class Apply(
    val id: Int,
    val pendingTeacherId: Int,
    val state: String,
) {
    init {
        require(id >= 0) { "Id must be positive" }
        require(pendingTeacherId >= 0) { "Pending Teacher Id must be positive" }
        require(state == "Pending" || state == "Accepted" || state == "Rejected") { "State must be Pending, Accepted or Rejected" }
    }
}
