package com.isel.leic.ps.ion_classcode.http.model.problem

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

data class ErrorMessageModel(
    val type: URI,
    val title: String,
    val detail: String,
)

class Problem {

    companion object {

        private const val BASE_URL = "https://github.com/i-on-project/repohouse/tree/main/docs/problems"
        private const val MEDIA_TYPE = "application/problem+json"

        private fun response(status: HttpStatus, problem: ErrorMessageModel): ResponseEntity<ErrorMessageModel> =
            ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body(problem)

        val stateMismatch = response(HttpStatus.UNAUTHORIZED, ErrorMessageModel(
            URI("$BASE_URL/state-mismatch"),
            "Authentication Failed.",
            "This response as been originated as a security measure."
        ))

        val invalidTeacherInput = response(HttpStatus.BAD_REQUEST, ErrorMessageModel(
            URI("$BASE_URL/invalid-teacher-input"),
            "Invalid Payload Body",
            "Your request body does not follow the specification"
        ))
    }
}