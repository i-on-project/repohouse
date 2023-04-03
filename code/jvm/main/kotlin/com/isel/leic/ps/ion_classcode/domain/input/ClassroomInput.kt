package com.isel.leic.ps.ion_classcode.domain.input

data class ClassroomInput(
    val name: String,
    val courseId: Int,
) {
    init {
        require(name.isNotBlank()) { "Classroom name must not be blank" }
        require(courseId > 0) { "Classroom course id must be greater than 0" }
    }
}
