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
import kotlin.reflect.KClass
import kotlin.reflect.cast

@ControllerAdvice
class FailureAdvice : AbstractMappingJacksonResponseBodyAdvice() {
    override fun beforeBodyWriteInternal(
        bodyContainer: MappingJacksonValue,
        contentType: MediaType,
        returnType: MethodParameter,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ) {
        bodyContainer.value.takeAs(Either::class)
            ?.mapLeft { it.takeAs(Failure::class) ?: Failure.INTERNAL_SERVER_ERROR }
            ?.peekLeft { response.setStatusCode(it.status) }
    }

    companion object {
        private fun <T : Any> Any.takeAs(kClass: KClass<T>): T? = this.takeIf { kClass.isInstance(it) }?.let { kClass.cast(it) }
    }
}