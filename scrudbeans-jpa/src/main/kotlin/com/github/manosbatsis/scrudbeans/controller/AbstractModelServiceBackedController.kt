package com.github.manosbatsis.scrudbeans.controller

import com.github.manosbatsis.kotlin.utils.api.Dto
import com.github.manosbatsis.scrudbeans.exceptions.ChildEntityNotFoundException
import com.github.manosbatsis.scrudbeans.model.BaseEntity
import com.github.manosbatsis.scrudbeans.service.JpaPersistableModelService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

open class AbstractModelServiceBackedController<
		T : BaseEntity<S>, S, SRV: JpaPersistableModelService<T, S>, DTO: Dto<T>
		> : IGenericController<T, S, DTO> {

	@Autowired
	lateinit var business: SRV

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
		@Parameter(description = "The field to use for sorting", required = false, example = "updated")
		@RequestParam("sort", required = false, defaultValue = "id")
		sortBy: String,
		@Parameter(description = "The sort direction, either ASC or DESC", required = false, example = "DESC")
		@RequestParam("direction", required = false, defaultValue = "DESC")
		sortDirection: Sort.Direction
	): ResponseEntity<Iterable<T>> {
		return ResponseEntity.ok(
			business.findAll( filter, sortBy, sortDirection, pn, ps))
	}

	@GetMapping("{id}")
	override fun findById(@PathVariable id: S): ResponseEntity<T> {
		return ResponseEntity.ok(business.getById(id))
	}

	@GetMapping("{id}/{child}")
	override fun findChildById(@PathVariable id: S, @PathVariable child: String): ResponseEntity<Any> {
		val childValue = business.findChildById(id, child)
		childValue?.let {
			return ResponseEntity.ok(it)
		}

		throw ChildEntityNotFoundException()
	}

	@PostMapping
	override fun save(@RequestBody entity: T): ResponseEntity<T> {
		return ResponseEntity.status(HttpStatus.CREATED).body(business.save(entity))
	}

	@PutMapping("{id}")
	override fun update(@RequestBody dto: DTO, @PathVariable id: S): ResponseEntity<T> {
		return business.update(dto, id)
			.let { ResponseEntity.ok(it) }
	}

	@PatchMapping("{id}")
	override fun updateNonNullFields(@RequestBody entity: T, @PathVariable id: S): ResponseEntity<T> {
		TODO("Non-implemented")
	}

	@DeleteMapping("{id}")
	override fun deleteById(@PathVariable id: S): ResponseEntity<Void> {
		return business.deleteById(id)
			.let { ResponseEntity.noContent().build() }
	}

	@RequestMapping("{id}", method = [RequestMethod.HEAD])
	override fun existsById(@PathVariable id: S): ResponseEntity<T> {
		return business.existsById(id)
			.let { ResponseEntity.ok().build() }
	}
}