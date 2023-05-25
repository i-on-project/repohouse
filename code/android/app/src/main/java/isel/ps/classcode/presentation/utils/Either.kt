package isel.ps.classcode.presentation.utils

/**
 * This code was made by the teacher Pedro Felix in DAW class.
 * https://github.com/isel-leic-daw/s2223i-51d-51n-public/blob/main/code/tic-tac-tow-service/src/main/kotlin/pt/isel/daw/tictactow/Either.kt
 */
sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()
}
