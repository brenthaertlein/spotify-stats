package com.nodemules.spotify.stats.config

import com.nodemules.spotify.stats.Failure
import io.vavr.control.Either
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice

@ControllerAdvice
class FailureAdvice : AbstractMappingJacksonResponseBodyAdvice() {
    override fun beforeBodyWriteInternal(
        bodyContainer: MappingJacksonValue,
        contentType: MediaType,
        returnType: MethodParameter,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ) {
        bodyContainer.value.takeIf { it is Either<*, *> }
            ?.let { it as Either<*, *> }
            ?.mapLeft {
                when (it) {
                    is Failure -> it
                    else -> Failure(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to decode error")
                }
            }
            ?.peekLeft { response.setStatusCode(it.httpStatus) }
    }
}