package com.isel.leic.ps.ionClassCode.repository.jdbi.transaction

import com.isel.leic.ps.ionClassCode.repository.transaction.Transaction
import com.isel.leic.ps.ionClassCode.repository.transaction.TransactionManager
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

/**
 * Implementation of the Transaction Manager
 */
@Component
class JdbiTransactionManager(
    private val jdbi: Jdbi
) : TransactionManager {
    override fun <R> run(block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = JdbiTransaction(handle)
            block(transaction)
        }
}
