package com.isel.leic.ps.ion_classcode.utils

import com.isel.leic.ps.ion_classcode.repository.jdbi.configure
import com.isel.leic.ps.ion_classcode.repository.jdbi.transaction.JdbiTransaction
import com.isel.leic.ps.ion_classcode.repository.transaction.Transaction
import com.isel.leic.ps.ion_classcode.repository.transaction.TransactionManager
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource

private val jdbi: () -> Jdbi = {
    val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
    Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(jdbcDatabaseURL)
        },
    ).configure()
}

fun testWithHandleAndRollback(block: (Handle) -> Unit) = jdbi().useTransaction<Exception> { handle ->
    block(handle)
    handle.rollback()
}

fun testWithTransactionManagerAndRollback(block: (TransactionManager) -> Unit) = jdbi().useTransaction<Exception>
{ handle ->

    val transaction = JdbiTransaction(handle)

    // a test TransactionManager that never commits
    val transactionManager = object : TransactionManager {
        override fun <R> run(block: (Transaction) -> R): R {
            return block(transaction)
            // n.b. no commit happens
        }
    }
    block(transactionManager)

    // finally, we rollback everything
    handle.rollback()
}
