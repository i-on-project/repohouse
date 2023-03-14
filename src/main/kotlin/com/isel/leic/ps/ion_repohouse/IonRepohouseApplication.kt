package com.isel.leic.ps.ion_repohouse

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class IonRepohouseApplication {

    @Bean
    fun okHttpClient() = OkHttpClient()

    @Bean
    fun jackson(): ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}



fun main(args: Array<String>) {
    runApplication<IonRepohouseApplication>(*args)
}
