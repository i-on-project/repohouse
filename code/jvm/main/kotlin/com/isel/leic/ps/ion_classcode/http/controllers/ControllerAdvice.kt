package com.isel.leic.ps.ion_classcode.http.controllers

import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.pipeline.AuthenticationFailed
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

    @ExceptionHandler
    fun handleUnauthorized(e: AuthenticationFailed) = Problem.unauthenticated

    @ExceptionHandler
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException) = Problem.methodNotAllowed

    @ExceptionHandler
    fun handleBodyParseFail(e: HttpMessageNotReadableException) = Problem.invalidInput

    @ExceptionHandler
    fun handleUnsupportedMediaType(e: UnsupportedMediaTypeStatusException) = Problem.unsupportedMediaType
}
