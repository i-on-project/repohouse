package com.isel.leic.ps.ionClassCode.repository.transaction

/**
 * Management of the transaction with the run block
 */
interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}
