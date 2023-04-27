package com.isel.leic.ps.ion_classcode.utils

//TODO: Just in usage, for transition to Result
sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()
}