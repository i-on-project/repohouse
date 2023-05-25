package com.isel.leic.ps.ionClassCode.domain

/**
 * Repository Domain Interface
 */
data class Repo(
    val id: Int,
    val url: String?,
    val name: String,
    val isCreated: Boolean,
)
