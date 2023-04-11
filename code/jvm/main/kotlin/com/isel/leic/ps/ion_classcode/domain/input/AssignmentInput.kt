package com.isel.leic.ps.ion_classcode.domain.input

/**
 * Assignment Input Interface
 */
data class AssignmentInput(
    val classroomId: Int,
    val maxElemsPerGroup: Int,
    val maxNumberGroups: Int,
    val description: String,
    val title: String,
) {
    init {
        require(classroomId > 0) { "Classroom id must be greater than 0" }
        require(maxElemsPerGroup > 0) { "Max number of elements must be greater than 0" }
        require(maxNumberGroups > 0) { "Max number of groups must be greater than 0" }
        require(description.isNotBlank()) { "Assigment description cannot be blank" }
        require(title.isNotBlank()) { "Assigment title cannot be blank" }
    }
}
