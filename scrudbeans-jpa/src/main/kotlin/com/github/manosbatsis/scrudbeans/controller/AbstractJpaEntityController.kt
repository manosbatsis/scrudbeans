package com.github.manosbatsis.scrudbeans.controller

import com.github.manosbatsis.kotlin.utils.api.Dto
import com.github.manosbatsis.scrudbeans.exceptions.ChildEntityNotFoundException
import com.github.manosbatsis.scrudbeans.service.JpaEntityService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

open class AbstractJpaEntityController<
    T : Any, S : Any, SRV : JpaEntityService<T, S>, DTO : Dto<T>
    > : JpaEntityController<T, S, DTO> {

    @Autowired
    lateinit var service: SRV

    @GetMapping
    override fun find(
        @Parameter(description = "The RSQL filter to apply as search  criteria", required = false)
        @RequestParam(required = false, defaultValue = "")
        filter: String,
        @Parameter(description = "The zero-indexed page number, optional", required = false, example = "0")
        @RequestParam("pn", required = false, defaultValue = "0")
        pn: Int,
        @Parameter(description = "The page size, optional with default being 10", required = false, example = "10")
        @RequestParam("ps", required = false, defaultValue = "10")
        ps: Int,
        @Parameter(description = "The field to use for sorting", required = false)
        @RequestParam("sort", required = false)
        sortBy: String?,
        @Parameter(description = "The sort direction, either ASC or DESC", required = false, example = "DESC")
        @RequestParam("direction", required = false, defaultValue = "DESC")
        sortDirection: Sort.Direction
    ): ResponseEntity<Iterable<T>> = doFind(filter, pn, ps, sortBy, sortDirection)

    /** Allows overriding the implementation of [find] */
    open fun doFind(
        filter: String,
        pn: Int,
        ps: Int,
        sortBy: String?,
        sortDirection: Sort.Direction
    ): ResponseEntity<Iterable<T>> {
        return ResponseEntity.ok(
            service.findAll(filter, sortBy, sortDirection, pn, ps)
        )
    }

    @GetMapping("{id}")
    override fun findById(@PathVariable id: S): ResponseEntity<T> = doFindById(id)

    /** Allows overriding the implementation of [findById] */
    open fun doFindById(id: S): ResponseEntity<T> {
        return ResponseEntity.ok(service.getById(id))
    }

    @GetMapping("{id}/{child}")
    override fun findChildById(
        @PathVariable id: S,
        @PathVariable child: String
    ): ResponseEntity<Any> = doFindChildById(id, child)

    /** Allows overriding the implementation of [findChildById] */
    open fun doFindChildById(id: S, child: String): ResponseEntity<Any> {
        return service.findChildById(id, child)
            ?.let { ResponseEntity.ok(it) }
            ?: throw ChildEntityNotFoundException()
    }

    @PostMapping
    override fun save(@RequestBody entity: T): ResponseEntity<T> = doSave(entity)

    /** Allows overriding the implementation of [save] */
    open fun doSave(entity: T): ResponseEntity<T> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.save(entity))

    @PutMapping("{id}")
    override fun update(
        @RequestBody entity: T,
        @PathVariable id: S
    ): ResponseEntity<T> = doUpdate(entity, id)

    /** Allows overriding the implementation of [update] */
    open fun doUpdate(entity: T, id: S): ResponseEntity<T> =
        if (service.identifierAdapter.getId(entity) == id)
            service.save(entity).let { ResponseEntity.ok(it) }
        else throw IllegalArgumentException("Matching request body and path variable IDs are required")

    @PatchMapping("{id}")
    override fun partialUpdate(
        @RequestBody dto: DTO,
        @PathVariable id: S
    ): ResponseEntity<T> = doPartialUpdate(dto, id)

    /** Allows overriding the implementation of [partialUpdate] */
    open fun doPartialUpdate(dto: DTO, id: S): ResponseEntity<T> =
        service.partialUpdate(dto, id).let { ResponseEntity.ok(it) }

    @DeleteMapping("{id}")
    override fun deleteById(@PathVariable id: S): ResponseEntity<Void> =
        doDeleteById(id)

    /** Allows overriding the implementation of [deleteById] */
    open fun doDeleteById(id: S): ResponseEntity<Void> =
        service.deleteById(id).let { ResponseEntity.noContent().build() }

    @RequestMapping("{id}", method = [RequestMethod.HEAD])
    override fun head(@PathVariable id: S): ResponseEntity<T> = doHead(id)

    /** Allows overriding the implementation of [head] */
    open fun doHead(id: S): ResponseEntity<T> =
        if(service.existsById(id)) ResponseEntity.ok().build() else ResponseEntity.notFound().build()
}
