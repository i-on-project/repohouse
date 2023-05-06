package com.isel.leic.ps.ion_classcode.utils

import com.isel.leic.ps.ion_classcode.repository.jdbi.configure
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
