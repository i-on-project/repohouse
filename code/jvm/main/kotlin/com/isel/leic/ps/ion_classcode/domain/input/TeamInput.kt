package com.isel.leic.ps.ion_classcode.domain.input

data class TeamInput(
    val name: String,
    val assigmentId: Int,
    val isCreated: Boolean,
) {
    init {
        require(name.isNotBlank()) { "Team name cannot be blank" }
        require(assigmentId > 0) { "Assigment id must be greater than 0" }
    }
}
