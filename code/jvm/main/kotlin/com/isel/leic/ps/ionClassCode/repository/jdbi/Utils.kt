package com.isel.leic.ps.ionClassCode.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin

/**
 * Plugins for the JDBI configuration
 */
fun Jdbi.configure(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    return this
}
