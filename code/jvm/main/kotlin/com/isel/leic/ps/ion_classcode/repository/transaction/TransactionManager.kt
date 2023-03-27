package com.isel.leic.ps.ion_classcode.repository.transaction

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}
