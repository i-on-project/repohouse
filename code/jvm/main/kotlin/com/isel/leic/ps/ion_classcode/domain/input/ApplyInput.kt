package com.isel.leic.ps.ion_classcode.domain.input

/**
 * Apply Input Interface
 */
data class ApplyInput(
    val pendingTeacherId: Int
){
    fun isNotValid(): Boolean {
        return pendingTeacherId <= 0
    }
}
