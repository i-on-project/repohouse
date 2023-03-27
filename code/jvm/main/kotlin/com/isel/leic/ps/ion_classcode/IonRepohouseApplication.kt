package com.isel.leic.ps.ion_classcode

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.isel.leic.ps.ion_classcode.http.pipeline.LoggerFilter
import com.isel.leic.ps.ion_classcode.repository.jdbi.configure
import okhttp3.OkHttpClient
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

const val DATABASE_URL = "JDBC_DATABASE_URL"

@SpringBootApplication
class IonClassCodeApplication : WebMvcConfigurer {
    @Bean
    fun jdbi(): Jdbi {
        val jdbcDatabaseURL = System.getenv(DATABASE_URL)
        return Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(jdbcDatabaseURL)
            },
        ).configure()
    }

    @Bean
    fun okHttpClient() = OkHttpClient()

    @Bean
    fun jackson(): ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    @Bean
    fun getLogger(): Logger = LoggerFactory.getLogger(LoggerFilter::class.java)
}

fun main(args: Array<String>) {
    runApplication<IonClassCodeApplication>(*args)
}
