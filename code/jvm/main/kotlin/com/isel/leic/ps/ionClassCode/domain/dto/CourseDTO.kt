package com.isel.leic.ps.ionClassCode.domain.dto

/**
 * Course Data Transfer Object
 */
data class CourseDTO(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val orgId: Long,
    val teachers: List<Int>,
    val isArchived: Boolean = false,
)
