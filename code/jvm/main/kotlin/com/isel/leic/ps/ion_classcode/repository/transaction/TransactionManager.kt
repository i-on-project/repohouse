package com.isel.leic.ps.ion_classcode.repository.transaction

/**
 * Management of the transaction with the run block
 */
interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}
