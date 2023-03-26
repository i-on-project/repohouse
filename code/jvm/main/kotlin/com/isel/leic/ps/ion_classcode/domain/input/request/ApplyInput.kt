package com.isel.leic.ps.ion_classcode.domain.input.request

data class ApplyInput(
    val teacherId:Int,
    override val composite:Int? = null,
    override val creator: Int
):RequestInput {
    init {
        require(teacherId > 0) { "Teacher id must be greater than 0" }
    }
}


