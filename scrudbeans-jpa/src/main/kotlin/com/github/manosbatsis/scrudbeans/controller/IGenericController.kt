package com.github.manosbatsis.scrudbeans.controller

import com.github.manosbatsis.kotlin.utils.api.Dto
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity

interface IGenericController<T : Any, S, DTO : Dto<T>> {
    fun find(
        filter: String,
        pn: Int,
        ps: Int,
        sortBy: String,
        sortDirection: Sort.Direction
    ): ResponseEntity<Iterable<T>>
    fun findById(id: S): ResponseEntity<T>
    fun findChildById(id: S, child: String): ResponseEntity<Any>
    fun save(entity: T): ResponseEntity<T>
    fun update(dto: DTO, id: S): ResponseEntity<T>
    fun updateNonNullFields(entity: T, id: S): ResponseEntity<T>
    fun deleteById(id: S): ResponseEntity<Void>
    fun existsById(id: S): ResponseEntity<T>
}