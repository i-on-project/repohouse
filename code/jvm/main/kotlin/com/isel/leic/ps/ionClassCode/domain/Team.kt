package com.isel.leic.ps.ionClassCode.domain

/**
 * Team Domain Interface
 */
data class Team(
    val id: Int,
    val name: String,
    val isCreated: Boolean,
    val isClosed: Boolean,
    val assignment: Int
)
