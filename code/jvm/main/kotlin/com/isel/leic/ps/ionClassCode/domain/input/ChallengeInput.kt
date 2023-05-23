package com.isel.leic.ps.ionClassCode.domain.input

/**
 * Challenge Input Interface
 */
data class ChallengeInput(
    val code: String,
    val state: String,
    val secret: String,
)
