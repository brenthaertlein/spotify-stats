package com.nodemules.spotify.stats.config

import com.nodemules.spotify.stats.Failure
import io.vavr.control.Either
import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ValidationAdvice {

    @ExceptionHandler(BindException::class)
    fun bindException(e: BindException): Either<Failure, Any> = Either.left(
        Failure.GenericFailure(
            HttpStatus.BAD_REQUEST,
            e.bindingResult.fieldErrors.map { it.defaultMessage }.joinToString("; ")
        )
    )
}
