package com.isel.leic.ps.ion_classcode.domain.input

data class DeliveryInput(
    val dueDate: Long,
    val assigmentId: Int,
    val tagControl: String,
) {
    init {
        require(dueDate > 0) { "Due date must be greater than 0" }
        require(assigmentId > 0) { "Assigment id must be greater than 0" }
        require(tagControl.isNotEmpty()) { "Tag control must not be empty" }
    }
}
