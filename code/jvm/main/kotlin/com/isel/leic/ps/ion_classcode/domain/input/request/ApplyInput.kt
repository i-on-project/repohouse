package com.isel.leic.ps.ion_classcode.domain.input.request

/**
 * Apply Request Input Interface
 */
data class ApplyInput(
    override val composite: Int? = null,
) : RequestInputInterface {

    fun isNotValid(): Boolean {
        return composite != null && composite <= 0
    }
}
