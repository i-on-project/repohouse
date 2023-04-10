package com.isel.leic.ps.ion_classcode.domain.input

data class ClassroomInput(
    val name: String,
    val courseId: Int,
    val teacherId: Int
) {
    fun isNotValid(): Boolean {
        return !(name.isNotBlank() && courseId > 0 && teacherId > 0)
    }
}
