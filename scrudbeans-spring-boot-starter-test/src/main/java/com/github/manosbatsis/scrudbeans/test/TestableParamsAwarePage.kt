package com.github.manosbatsis.scrudbeans.test

import com.github.manosbatsis.scrudbeans.api.util.ParamsAwarePage
import org.apache.commons.lang3.NotImplementedException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.function.Function

open class TestableParamsAwarePage<T> : ParamsAwarePage<T> {
    private val size = 0
    override fun getSize(): Int = size

    private val number = 0
    override fun getNumber(): Int = number

    private val totalPages = 0
    override fun getTotalPages(): Int = totalPages

    private val numberOfElements = 0
    override fun getNumberOfElements(): Int = numberOfElements

    private val totalElements: Long = 0
    override fun getTotalElements(): Long = totalElements

    override var parameters: Map<String, Array<String>> = emptyMap()

    private var content: List<T> = emptyList()
    override fun getContent(): List<T> = content

    override fun hasContent(): Boolean = !content.isEmpty()

    private var sort: Sort = Sort.by(Sort.DEFAULT_DIRECTION)
    override fun getSort(): Sort = sort

    private val first: Boolean = number == 0
    override fun isFirst(): Boolean = first

    override operator fun hasNext(): Boolean {
        return getNumber() + 1 < getTotalPages()
    }

    override fun hasPrevious(): Boolean {
        return !isFirst
    }

    override fun nextPageable(): Pageable {
        return if (hasNext()) PageRequest.of(number, size, Sort.DEFAULT_DIRECTION).next() else Pageable.unpaged()
    }

    override fun previousPageable(): Pageable {
        return if (hasPrevious()) PageRequest.of(number, size, Sort.DEFAULT_DIRECTION)
            .previousOrFirst() else Pageable.unpaged()
    }

    private val isLast: Boolean = !hasNext()
    override fun isLast(): Boolean = isLast

    override operator fun iterator(): MutableIterator<T> {
        return this.getContent().toMutableList().iterator()
    }

    override fun <U : Any?> map(converter: Function<in T, out U>): Page<U> {
        throw NotImplementedException("Non-implemented")
    }
}