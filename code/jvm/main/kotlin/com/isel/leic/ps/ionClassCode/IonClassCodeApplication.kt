package com.isel.leic.ps.ionClassCode

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.isel.leic.ps.ionClassCode.http.pipeline.AuthenticationInterceptor
import com.isel.leic.ps.ionClassCode.http.pipeline.LoggerFilter
import com.isel.leic.ps.ionClassCode.http.pipeline.UserArgumentResolver
import com.isel.leic.ps.ionClassCode.repository.jdbi.configure
import com.isel.leic.ps.ionClassCode.tokenHash.GenericTokenHash
import okhttp3.OkHttpClient
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

const val DATABASE_URL = "JDBC_DATABASE_URL"

@SpringBootApplication
@EnableScheduling
class IonClassCodeApplication : WebMvcConfigurer {
    @Bean
    fun jdbi() = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(System.getenv(DATABASE_URL))
        },
    ).configure()

    @Bean
    fun okHttpClient() = OkHttpClient()
    @Bean
    fun jackson(): ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    @Bean
    fun getLogger(): Logger = LoggerFactory.getLogger(LoggerFilter::class.java)

    @Bean
    fun getTokenHash() = GenericTokenHash("SHA256")
}

@Configuration
class PipelineConfigurer(
    val userArgumentResolver: UserArgumentResolver,
    val authenticationInterceptor: AuthenticationInterceptor,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(userArgumentResolver)
    }
}

fun main(args: Array<String>) {
    runApplication<IonClassCodeApplication>(*args)
}
