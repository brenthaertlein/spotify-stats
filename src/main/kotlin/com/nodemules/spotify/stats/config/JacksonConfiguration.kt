package com.nodemules.spotify.stats.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.node.TreeTraversingParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import io.vavr.control.Either
import io.vavr.control.Try
import mu.KLogging
import org.springframework.boot.jackson.JsonComponent
import org.springframework.boot.jackson.JsonObjectDeserializer
import org.springframework.boot.jackson.JsonObjectSerializer
import org.springframework.http.HttpStatus

@JsonComponent
class JacksonConfiguration {

    class EitherSerializer : JsonObjectSerializer<Either<*, *>>() {

        private val objectMapper = jacksonObjectMapper()

        private fun Any.toFailure() = when (this) {
            is Failure -> this
            is SpotifyErrorResponse -> this.error.run { Failure(HttpStatus.valueOf(status), message) }
            else -> run {
                logger.error { "Unable to serialize $this" }
                Failure(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR, "Serialization error")
            }
        }

        override fun serializeObject(value: Either<*, *>, jgen: JsonGenerator, provider: SerializerProvider) {
            Try.of {
                value
                    .fold({ it.toFailure() }) { it }
                    .let { objectMapper.readTree(objectMapper.writeValueAsString(it)).fields() }
                    .forEachRemaining { (key, value) -> jgen.writeObjectField(key, value) }
            }
                .onFailure { logger.error(it) { "Error serializing controller response" } }
        }

        companion object : KLogging()
    }

    class EitherDeserializer : ContextualDeserializer, JsonObjectDeserializer<Either<*, *>>() {

        private var type: JavaType? = null
        private var leftType: JavaType? = null

        override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> {
            return EitherDeserializer().apply {
                type = ctxt.contextualType.containedTypeOrUnknown(1)
                leftType = ctxt.contextualType.containedTypeOrUnknown(0)
            }
        }

        override fun deserializeObject(
            jsonParser: JsonParser,
            context: DeserializationContext,
            codec: ObjectCodec,
            tree: JsonNode?
        ): Either<*, *> = tree?.let {
            Try.of { codec.readValue<Any>(TreeTraversingParser(tree), type) }
                .map { Either.right<Any, Any>(it) }
                .getOrElseGet { Either.left(codec.readValue(TreeTraversingParser(tree), leftType)) }
        } ?: Either.left<Failure, Any>(Failure(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR, "Unable to deserialize"))

    }
}