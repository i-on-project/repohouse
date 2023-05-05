package com.isel.leic.ps.ion_classcode.domain.input

/**
 * Course Input Interface
 */
data class CourseInput(
    val orgUrl: String,
    val name: String,
    val orgId: Long,
    val teacherId: Int,
)
