package com.isel.leic.ps.ionClassCode.http

import com.isel.leic.ps.ionClassCode.http.controllers.web.POSITION_COOKIE_NAME
import com.isel.leic.ps.ionClassCode.http.controllers.web.STATE_COOKIE_NAME
import com.isel.leic.ps.ionClassCode.http.controllers.web.STATE_COOKIE_PATH
import com.isel.leic.ps.ionClassCode.http.controllers.web.STUDENT_COOKIE_NAME
import com.isel.leic.ps.ionClassCode.http.controllers.web.TEACHER_COOKIE_NAME
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTests {

    @LocalServerPort
    var port: Int = 0

    @Test
    fun `auth teacher`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        client.get().uri(Uris.AUTH_TEACHER_PATH)
            .exchange()
            .expectStatus().isOk
            .expectCookie()
            .exists(STATE_COOKIE_NAME)
            .expectCookie()
            .secure(STATE_COOKIE_NAME, true)
            .expectCookie()
            .httpOnly(STATE_COOKIE_NAME, true)
            .expectCookie()
            .path(STATE_COOKIE_NAME, STATE_COOKIE_PATH)
            .expectCookie()
            .exists(POSITION_COOKIE_NAME)
            .expectCookie()
            .secure(POSITION_COOKIE_NAME, true)
            .expectCookie()
            .httpOnly(POSITION_COOKIE_NAME, true)
            .expectCookie()
            .path(POSITION_COOKIE_NAME, Uris.API)
            .expectCookie()
            .valueEquals(POSITION_COOKIE_NAME, TEACHER_COOKIE_NAME)
    }

    @Test
    fun `auth student`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        client.get().uri(Uris.AUTH_STUDENT_PATH)
            .exchange()
            .expectStatus().isOk
            .expectCookie()
            .exists(STATE_COOKIE_NAME)
            .expectCookie()
            .secure(STATE_COOKIE_NAME, true)
            .expectCookie()
            .httpOnly(STATE_COOKIE_NAME, true)
            .expectCookie()
            .path(STATE_COOKIE_NAME, STATE_COOKIE_PATH)
            .expectCookie()
            .exists(POSITION_COOKIE_NAME)
            .expectCookie()
            .secure(POSITION_COOKIE_NAME, true)
            .expectCookie()
            .httpOnly(POSITION_COOKIE_NAME, true)
            .expectCookie()
            .path(POSITION_COOKIE_NAME, Uris.API)
            .expectCookie()
            .valueEquals(POSITION_COOKIE_NAME, STUDENT_COOKIE_NAME)
    }
}
