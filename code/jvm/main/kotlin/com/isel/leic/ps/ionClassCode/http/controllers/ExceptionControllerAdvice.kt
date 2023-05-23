package com.isel.leic.ps.ionClassCode.http.controllers

import com.isel.leic.ps.ionClassCode.http.model.problem.Problem
import com.isel.leic.ps.ionClassCode.http.pipeline.AuthenticationFailed
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.UnsupportedMediaTypeStatusException

/**
 * Controller Advice that handles exceptions thrown by the controllers.
 */
@ControllerAdvice
class ExceptionControllerAdvice {

    /**
     * Handles the [AuthenticationFailed] exception.
     */
    @ExceptionHandler
    fun handleUnauthorized(e: AuthenticationFailed) = Problem.unauthenticated

    /**
     * Handles the [HttpRequestMethodNotSupportedException] exception.
     */
    @ExceptionHandler
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException) = Problem.methodNotAllowed

    /**
     * Handles the [HttpMessageNotReadableException] exception.
     */
    @ExceptionHandler
    fun handleBodyParseFail(e: HttpMessageNotReadableException) = Problem.invalidInput

    /**
     * Handles the [UnsupportedMediaTypeStatusException] exception.
     */
    @ExceptionHandler
    fun handleUnsupportedMediaType(e: UnsupportedMediaTypeStatusException) = Problem.unsupportedMediaType
}
