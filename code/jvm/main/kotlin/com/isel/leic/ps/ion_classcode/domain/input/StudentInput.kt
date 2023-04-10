package com.isel.leic.ps.ion_classcode.domain.input

data class StudentInput(
    val name: String, //
    val email: String,
    val githubUsername: String,
    val schoolId: Int? = null,
    val token: String,
    val githubId: Long,
) {
    fun isNotValid(): Boolean {
        val schoolIdIsValid = schoolId == null || schoolId > 0
        return !(name.isNotEmpty() && schoolIdIsValid && email.isNotEmpty() && githubUsername.isNotEmpty() && token.isNotEmpty() && githubId >= 0)
    }
}
