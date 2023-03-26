package com.isel.leic.ps.ion_classcode

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.isel.leic.ps.ion_classcode.http.pipeline.LoggerFilter
import okhttp3.OkHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class IonRepohouseApplication : WebMvcConfigurer {
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
    runApplication<IonRepohouseApplication>(*args)
}
