package com.isel.leic.ps.ionClassCode.domain.input

/**
 * Teacher Input Interface
 */
data class TeacherInput(
    val email: String,
    val githubUsername: String,
    val githubId: Long,
    val token: String,
    val name: String,
    val githubToken: String,
) {
    fun isNotValid(): Boolean {
        return !(name.isNotEmpty() && email.isNotEmpty() && githubUsername.isNotEmpty() && token.isNotEmpty() && githubId >= 0 && githubToken.isNotEmpty())
    }
}
