package com.isel.leic.ps.ionClassCode.http.model.problem

import com.isel.leic.ps.ionClassCode.http.model.output.OutputModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

/**
 * Represents a Error Message Model.
 */
data class ErrorMessageModel(
    val type: URI,
    val title: String,
    val detail: String,
) : OutputModel

/**
 * Represents a Problem.
 * This class contains all the possible problems that can occur in the application.
 * Each problem is represented by a [ResponseEntity] with the corresponding [HttpStatus] and [ErrorMessageModel].
 */
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

        val alreadyExists = response(
            HttpStatus.CONFLICT,
            ErrorMessageModel(
                URI("$BASE_URL/already-exists"),
                "Resource already exists.",
                "The resource you are trying to create already exists.",
            ),
        )

        val methodNotAllowed = response(
            HttpStatus.METHOD_NOT_ALLOWED,
            ErrorMessageModel(
                URI("$BASE_URL/internal-server-error"),
                "Method not supported.",
                "Consult the API documentation for supported methods.",
            ),
        )

        val unsupportedMediaType = response(
            HttpStatus.METHOD_NOT_ALLOWED,
            ErrorMessageModel(
                URI("$BASE_URL/internal-server-error"),
                "Media Type not supported",
                "Consult the API documentation for supported media types",
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
                "The resource you are trying to access is forbidden because of your authentication status.",
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

        fun cooldown(time: Int) = response(
            HttpStatus.REQUEST_TIMEOUT,
            ErrorMessageModel(
                URI("$BASE_URL/too-many-requests"),
                "In Cooldown.",
                "You have to wait $time seconds for another request.",
            ),
        )
    }
}