package com.isel.leic.ps.ion_classcode.http.model.problem

import com.isel.leic.ps.ion_classcode.http.model.output.OutputModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

data class ErrorMessageModel(
    val type: URI,
    val title: String,
    val detail: String,
) : OutputModel

class Problem {

    companion object {

        private const val BASE_URL = "https://github.com/i-on-project/repohouse/tree/main/docs/problems"
        private const val MEDIA_TYPE = "application/problem+json"

        private fun response(status: HttpStatus, problem: ErrorMessageModel): ResponseEntity<ErrorMessageModel> =
            ResponseEntity
                .status(status)
                .header("Content-Type", MEDIA_TYPE)
                .body(problem)

        val internalError = response(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorMessageModel(
                URI("$BASE_URL/internal-server-error"),
                "An unexpected error as occurred.",
                "The server failed to process the request.",
            ),
        )

        val stateMismatch = response(
            HttpStatus.UNAUTHORIZED,
            ErrorMessageModel(
                URI("$BASE_URL/state-mismatch"),
                "Authentication Failed.",
                "This response as been originated as a security measure.",
            ),
        )

        val invalidInput = response(
            HttpStatus.BAD_REQUEST,
            ErrorMessageModel(
                URI("$BASE_URL/invalid-input"),
                "Invalid Payload Body.",
                "Your request body does not follow the specification.",
            ),
        )

        val invalidOperation = response(
            HttpStatus.BAD_REQUEST,
            ErrorMessageModel(
                URI("$BASE_URL/invalid-operation"),
                "Invalid Operation.",
                "The operation you are trying to perform is not valid.",
            ),
        )

        val notFound = response(
            HttpStatus.NOT_FOUND,
            ErrorMessageModel(
                URI("$BASE_URL/not-found"),
                "Resource not found.",
                "The resource does not exist.",
            ),
        )

        val conflict = response(
            HttpStatus.CONFLICT,
            ErrorMessageModel(
                URI("$BASE_URL/conflict"),
                "Conflict.",
                "The resource you are trying to create or update conflicts with one that already exists.",
            ),
        )

        val unauthorized = response(
            HttpStatus.UNAUTHORIZED,
            ErrorMessageModel(
                URI("$BASE_URL/unauthorized"),
                "Unauthorized Action.",
                "The resource you are trying to access is forbidden because of your user status.",
            ),
        )

        val userNotFound = response(
            HttpStatus.NOT_FOUND,
            ErrorMessageModel(
                URI("$BASE_URL/user-not-found"),
                "User not found.",
                "This user does not exist.",
            ),
        )

        val courseNotFound = response(
            HttpStatus.NOT_FOUND,
            ErrorMessageModel(
                URI("$BASE_URL/course-not-found"),
                "Course not found.",
                "This Course does not exist.",
            ),
        )

        val courseAlreadyExists = response(
            HttpStatus.CONFLICT,
            ErrorMessageModel(
                URI("$BASE_URL/course-already-exists"),
                "Course already exists.",
                "The name or url specified are already in use.",
            ),
        )

        val userInCourse = response(
            HttpStatus.CONFLICT,
            ErrorMessageModel(
                URI("$BASE_URL/user-in-course"),
                "User already in course.",
                "You are already in this course.",
            ),
        )

        val userNotInCourse = response(
            HttpStatus.CONFLICT,
            ErrorMessageModel(
                URI("$BASE_URL/user-not-in-course"),
                "User not in course.",
                "You can not leave a course where you are not listed.",
            ),
        )

        val notStudent = response(
            HttpStatus.UNAUTHORIZED,
            ErrorMessageModel(
                URI("$BASE_URL/not-student"),
                "Not a student.",
                "The resource you are trying to access is forbidden because of your user status.",
            ),
        )

        val notTeacher = response(
            HttpStatus.UNAUTHORIZED,
            ErrorMessageModel(
                URI("$BASE_URL/not-teacher"),
                "Not a teacher.",
                "The resource you are trying to access is forbidden because of your user status.",
            ),
        )

        val unauthenticated = response(
            HttpStatus.UNAUTHORIZED,
            ErrorMessageModel(
                URI("$BASE_URL/unauthenticated"),
                "Unauthenticated.",
                "The resource you are trying to access is forbidden because of your user status.",
            ),
        )

        val gone = response(
            HttpStatus.GONE,
            ErrorMessageModel(
                URI("$BASE_URL/gone"),
                "Gone.",
                "The resource you are trying to access is no longer available.",
            ),
        )

        val badRequest = response(
            HttpStatus.BAD_REQUEST,
            ErrorMessageModel(
                URI("$BASE_URL/bad-request"),
                "Bad Request.",
                "The request is not valid.",
            ),
        )

        val forbidden = response(
            HttpStatus.FORBIDDEN,
            ErrorMessageModel(
                URI("$BASE_URL/forbidden"),
                "Forbidden.",
                "The resource you are trying to access is forbidden.",
            ),
        )
    }
}
