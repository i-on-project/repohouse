package com.isel.leic.ps.ion_classcode.domain.dto

/**
 * Course Data Transfer Object
 */
data class CourseDTO(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teachers: List<Int>,
    val isArchived: Boolean = false
)
