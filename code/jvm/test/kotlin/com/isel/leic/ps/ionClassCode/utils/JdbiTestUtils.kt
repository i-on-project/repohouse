package com.isel.leic.ps.ionClassCode.utils

import com.isel.leic.ps.ionClassCode.repository.jdbi.configure
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
