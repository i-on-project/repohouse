package com.isel.leic.ps.ionClassCode.domain.input

/**
 * Course Input Interface
 */
data class CourseInput(
    val orgUrl: String,
    val name: String,
    val orgId: Long,
    val teacherId: Int,
)
