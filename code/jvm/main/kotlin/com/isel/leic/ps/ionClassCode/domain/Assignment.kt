package com.isel.leic.ps.ionClassCode.domain

import java.sql.Timestamp

/**
 * Assigment Domain Interface
 */
data class Assignment(
    val id: Int,
    val classroomId: Int,
    val minElemsPerGroup: Int,
    val maxElemsPerGroup: Int,
    val maxNumberGroups: Int,
    val releaseDate: Timestamp,
    val description: String,
    val title: String,
)
