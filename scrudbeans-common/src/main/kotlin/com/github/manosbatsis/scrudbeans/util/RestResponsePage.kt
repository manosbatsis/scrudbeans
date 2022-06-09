package com.github.manosbatsis.scrudbeans.util

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@JsonIgnoreProperties(ignoreUnknown = true)
class RestResponsePage<T> : PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    @Suppress("UNUSED_PARAMETER")
    constructor(
        @JsonProperty("content") content: List<T> = emptyList(),
        @JsonProperty("number") number: Int = 0,
        @JsonProperty("size") size: Int = 10,
        @JsonProperty("totalElements") totalElements: Long = 0,
        @JsonProperty("pageable") pageable: JsonNode?,
        @JsonProperty("last") last: Boolean,
        @JsonProperty("totalPages") totalPages: Int,
        @JsonProperty("sort") sort: JsonNode?,
        @JsonProperty("first") first: Boolean,
        @JsonProperty("numberOfElements") numberOfElements: Int
    ) : super(content, PageRequest.of(number, size), totalElements)

    constructor(
        content: List<T>,
        pageable: Pageable?,
        total: Long
    ) : super(content, pageable!!, total)

    constructor(content: List<T>) : super(content)
    constructor() : super(emptyList())
}