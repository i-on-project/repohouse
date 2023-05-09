package com.isel.leic.ps.ionClassCode.domain

import java.sql.Timestamp

/**
 * Classroom Domain Interface
 */
data class Classroom(
    val id: Int,
    val name: String,
    val lastSync: Timestamp,
    val inviteLink: String,
    val isArchived: Boolean,
    val courseId: Int,
)
