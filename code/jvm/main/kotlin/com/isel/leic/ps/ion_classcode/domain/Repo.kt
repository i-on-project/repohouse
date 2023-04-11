package com.isel.leic.ps.ion_classcode.domain

/**
 * Repository Domain Interface
 */
data class Repo(
    val id: Int,
    val url: String,
    val name: String,
    val isCreated: Boolean
)
