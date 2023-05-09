package com.isel.leic.ps.ionClassCode.utils

/**
 * This code was made by the teacher Pedro Felix in DAW class.
 * https://github.com/isel-leic-daw/s2223i-51d-51n-public/blob/main/code/tic-tac-tow-service/src/main/kotlin/pt/isel/daw/tictactow/Either.kt
 */
sealed class Result<out L, out R> {
    data class Problem<out L>(val value: L) : Result<L, Nothing>()
    data class Success<out R>(val value: R) : Result<Nothing, R>()
}
