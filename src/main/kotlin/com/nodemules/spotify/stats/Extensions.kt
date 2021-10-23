package com.nodemules.spotify.stats

import io.vavr.control.Either
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.support.PageableExecutionUtils

fun <T> Collection<T>.sample(): T? = this.asSequence().shuffled().find { true }

inline fun <L, R, R2> Either<out L, out R>.narrowFlatMap(crossinline mapper: (R) -> Either<out L, out R2>): Either<L, R2> =
    Either.narrow(this).flatMap { Either.narrow(mapper(it)) }

fun <L, R> Either<L, R>.flatMapLeft(leftMapper: (left: L) -> Either<L, R>): Either<L, R> = this.fold(leftMapper) { Either.right(it) }

fun <T> MongoTemplate.page(query: Query, entityClass: Class<T>, pageable: Pageable): Page<T> =
    PageableExecutionUtils.getPage(find(Query.of(query).with(pageable), entityClass), pageable) { count(query, entityClass) }

fun <T, R> MongoTemplate.page(
    aggregation: TypedAggregation<T>,
    pageable: Pageable,
    targetClass: Class<R>,
    countAggregation: () -> TypedAggregation<T>
): Page<R> =
    PageableExecutionUtils.getPage(aggregate(aggregation, targetClass).mappedResults, pageable) {
        aggregate(countAggregation(), Count::class.java).mappedResults[0].count
    }

private data class Count(val count: Long)
