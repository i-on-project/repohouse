package com.isel.leic.ps.ionClassCode.domain.input

/**
 * Apply Input Interface
 */
data class ApplyInput(
    val pendingTeacherId: Int,
) {
    fun isNotValid(): Boolean {
        return pendingTeacherId <= 0
    }
}
