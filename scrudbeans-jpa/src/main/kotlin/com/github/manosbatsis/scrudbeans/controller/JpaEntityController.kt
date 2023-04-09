package com.github.manosbatsis.scrudbeans.controller

import com.github.manosbatsis.kotlin.utils.api.Dto
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity

interface JpaEntityController<T : Any, S, DTO : Dto<T>> {
    fun find(
        filter: String,
        pn: Int,
        ps: Int,
        sortBy: String? = null,
        sortDirection: Sort.Direction,
    ): ResponseEntity<Iterable<T>>
    fun findById(id: S): ResponseEntity<T>
    fun findChildById(id: S, child: String): ResponseEntity<Any>
    fun save(entity: T): ResponseEntity<T>
    fun partialUpdate(dto: DTO, id: S): ResponseEntity<T>
    fun update(entity: T, id: S): ResponseEntity<T>
    fun deleteById(id: S): ResponseEntity<Void>
    fun head(id: S): ResponseEntity<T>
}
