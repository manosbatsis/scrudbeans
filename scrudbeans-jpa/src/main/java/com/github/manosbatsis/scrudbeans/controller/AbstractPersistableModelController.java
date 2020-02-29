/**
 *
 * ScrudBeans: Model driven development for Spring Boot
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.manosbatsis.kotlin.utils.api.Dto;
import com.github.manosbatsis.scrudbeans.api.exception.NotFoundException;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.domain.RawJson;
import com.github.manosbatsis.scrudbeans.hypermedia.util.HypermediaUtils;
import com.github.manosbatsis.scrudbeans.rsql.RsqlUtils;
import com.github.manosbatsis.scrudbeans.service.PersistableModelService;
import com.github.manosbatsis.scrudbeans.specification.SpecificationsBuilder;
import com.github.manosbatsis.scrudbeans.uischema.model.UiSchema;
import com.github.manosbatsis.scrudbeans.util.ParamsAwarePageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.*;


/**
 * Abstract REST controller using a service implementation
 * <p/>
 * <p>You should extend this class when you want to use a 3 layers pattern : Repository, Service and Controller
 * If you don't have a real service (also called business layer), consider using RepositoryBasedRestController</p>
 * <p/>
 * <p>Default implementation uses "id" field (usually a Long) in order to identify resources in web request.
 * If your want to identity resources by a slug (human readable identifier), your should override plainJsonGetById() method with for example :
 * <p/>
 * <pre>
 * <code>
 * {@literal @}Override
 * public Sample plainJsonGetById({@literal @}PathVariable String id) {
 * Sample sample = this.service.findByName(id);
 * if (sample == null) {
 * throw new NotFoundException();
 * }
 * return sample;
 * }
 * </code>
 * </pre>
 *
 * @param <T>  Your resource class to manage, maybe an entity or DTO class
 * @param <PK> EntityModel id type, usually Long or String
 * @param <S>  The service class
 */
public class AbstractPersistableModelController<T, PK extends Serializable, S extends PersistableModelService<T, PK>>
        extends AbstractModelServiceBackedController<T, PK, S, Dto<T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPersistableModelController.class);

    private SpecificationsBuilder<T, PK> specificationsBuilder;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.specificationsBuilder = new SpecificationsBuilder<T, PK>(this.modelType, this.service.getConversionService());
	}

	// Create
	// =====================
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(description = "Create a new resource")
	public T create(@RequestBody T resource) {
		return super.create(resource);
	}


	// Update
	// =====================
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	@Operation(description = "Update a resource")
	public T update(
			@Parameter(name = "id", required = true)
			@PathVariable PK id, @RequestBody T model) {
		return super.update(id, model);
	}

	// Patch
	// =====================
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	@Operation(
			summary = "Patch (partially update) a resource",
			description = "Partial updates will apply all given properties (ignoring null values) to the persisted entity.")
	public T patch(
			@Parameter(name = "id", required = true)
			@PathVariable PK id, @RequestBody T model) {
		return super.patch(id, model);
	}


	// Find all (no paging)
	// ========================
	@RequestMapping(method = RequestMethod.GET, params = "page=no")
	@Operation(
			summary = "Get the full collection of resources (no paging or criteria)",
			description = "Find all resources, and return the full collection (i.e. VS a page of the total results)")
	public Iterable<T> findAll() {
		return super.findAll();
	}


	// Search
	// ========================

	//@Override
	@RequestMapping(method = RequestMethod.GET)
	@Operation(summary = "Search for resources (paginated).", description = "Find all resources matching the given criteria and return a paginated collection."
			+ "Predefined paging properties are _pn (page number), _ps (page size) and sort. All serialized member names "
			+ "of the resource are supported as search criteria in the form of HTTP URL parameters.")
	public ParamsAwarePageImpl<T> findPaginated(
			@Parameter(name = SpecificationsBuilder.PARAM_FILTER, description = "The RSQL/FIQL query to use. Simply URL param based search will be used if missing.")
			@RequestParam(value = SpecificationsBuilder.PARAM_FILTER, required = false) String filter,
			@Parameter(name = SpecificationsBuilder.PARAM_PAGE_NUMBER, description = "The page number, default is 00")
			@RequestParam(value = SpecificationsBuilder.PARAM_PAGE_NUMBER, required = false, defaultValue = "0") Integer page,
			@Parameter(name = SpecificationsBuilder.PARAM_PAGE_SIZE, description = "The page size")
			@RequestParam(value = SpecificationsBuilder.PARAM_PAGE_SIZE, required = false, defaultValue = "10") Integer size,
			@Parameter(name = SpecificationsBuilder.PARAM_SORT, description = "Comma separated list of attribute names, descending for each one prefixed with a dash, ascending otherwise")
			@RequestParam(value = SpecificationsBuilder.PARAM_SORT, required = false, defaultValue = "id") String sort
	) {
		Pageable pageable = PageableUtil.buildPageable(page, size, sort);
		return this.<T>findPaginated(pageable, null);
	}

	// Read
	// ==============
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	@Operation(summary = "Find by id", description = "Find a resource by it's identifier")
	public T findById(@Parameter(name = "id", required = true) @PathVariable PK id) {
		T model = super.findById(id);
		if (model == null) {
			throw new NotFoundException();
		}
		return model;
	}

	/**
	 * GET has the same effect to both member and relationship endpoints
	 */
	@RequestMapping(value = {"{id}/{relationName}", "{id}/relationships/{relationName}"}, method = RequestMethod.GET)
	@Operation(summary = "Find related by root id", description = "Find the related resource for the given relation name and identifier")
	public ResponseEntity getRelated(
			@Parameter(name = SpecificationsBuilder.PARAM_PK, required = true) @PathVariable PK id,
			@Parameter(name = SpecificationsBuilder.PARAM_RELATION_NAME, required = true) @PathVariable String relationName,
			@Parameter(name = SpecificationsBuilder.PARAM_FILTER, description = "The RSQL/FIQL query to use. Simply URL param based search will be used if missing.")
			@RequestParam(value = SpecificationsBuilder.PARAM_FILTER, required = false) String filter,
			@Parameter(name = SpecificationsBuilder.PARAM_PAGE_NUMBER, description = "The page number")
			@RequestParam(value = SpecificationsBuilder.PARAM_PAGE_NUMBER, required = false, defaultValue = "0") Integer page,
			@Parameter(name = SpecificationsBuilder.PARAM_PAGE_SIZE, description = "The page size")
			@RequestParam(value = SpecificationsBuilder.PARAM_PAGE_SIZE, required = false, defaultValue = "10") Integer size,
			@Parameter(name = SpecificationsBuilder.PARAM_SORT, description = "Comma separated list of attribute names, descending for each one prefixed with a dash, ascending otherwise")
			@RequestParam(value = SpecificationsBuilder.PARAM_SORT, required = false, defaultValue = "id") String sort) {

		// get the field info for the relation, if any
		FieldInfo fieldInfo = this.getModelInfo().getField(relationName);

		// throw error if not valid or linkable relationship
		if (fieldInfo == null || !fieldInfo.isLinkableResource()) {
			throw new IllegalArgumentException("Invalid relationship: " + relationName);
		}

		// use response entity to accommodate different return types
		ResponseEntity responseEntity = null;

		// if ToOne
		if (fieldInfo.isToOne()) {
            Object related = this.findRelatedSingle(id, fieldInfo);
            // if found
			EntityModel res = HypermediaUtils.toHateoasResource(related, fieldInfo.getRelatedModelInfo());
            responseEntity = new ResponseEntity(res, HttpStatus.OK);
        }
		else if (fieldInfo.isOneToMany()) {
			Pageable pageable = PageableUtil.buildPageable(page, size, sort);
			ParamsAwarePageImpl resultsPage = this.findRelatedPaginated(id, pageable, fieldInfo);
			responseEntity = new ResponseEntity(resultsPage, HttpStatus.OK);

		}


		return responseEntity;
	}

	@RequestMapping(params = "ids", method = RequestMethod.GET)
	@Operation(summary = "Search by ids", description = "Find the set of resources matching the given identifiers.")
	public Iterable<T> findByIds(@RequestParam(value = "ids[]") Set<PK> ids) {
		return super.findByIds(ids);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete a resource", description = "Delete a resource by its identifier. ", method = "DELETE")
	public void delete(@Parameter(name = "id", required = true) @PathVariable PK id) {
		super.delete(id);
	}

	@RequestMapping(value = "jsonschema", method = RequestMethod.GET, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get JSON Schema", description = "Get the JSON Schema for the controller entity type")
	public RawJson getJsonSchema() throws JsonProcessingException {
		return super.getJsonSchema();
	}

	@RequestMapping(value = "uischema", method = RequestMethod.GET, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get UI schema", description = "Get the UI achema for the controller entity type, including fields, use-cases etc.")
	@Deprecated
	public UiSchema getUiSchema() {
		return super.getUiSchema();
	}


    /**
     * Find the other end of a ToOne relationship
     *
     * @param id        the root entity ID
     * @param fieldInfo the member/relation name
     * @return the single related entity, if any
     * @see PersistableModelService#findRelatedSingle(Serializable, FieldInfo)
     */
    protected Object findRelatedSingle(PK id, FieldInfo fieldInfo) {
        return this.service.findRelatedSingle(id, fieldInfo);
    }


    /**
     * Find a page of results matching the other end of a ToMany relationship
     *
     * @param id        the root entity ID
     * @param pageable  the page config
     * @param fieldInfo the member/relation name
     * @return the page of results, may be <code>null</code>
     * @see PersistableModelService#findRelatedPaginated(java.lang.Class, org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Pageable)
     */
    protected <M> ParamsAwarePageImpl<M> findRelatedPaginated(PK id, Pageable pageable, FieldInfo fieldInfo) {
        ParamsAwarePageImpl<M> page = null;
        Optional<String> reverseFieldName = fieldInfo.getReverseFieldName();
        if (reverseFieldName.isPresent()) {
            Map<String, String[]> params = request.getParameterMap();
            Map<String, String[]> implicitCriteria = new HashMap<>();
            implicitCriteria.put(reverseFieldName.get(), new String[]{id.toString()});

            ModelInfo relatedModelInfo = fieldInfo.getRelatedModelInfo();
            // optionally create a query specification
            Specification<M> spec = RsqlUtils.buildSpecification(relatedModelInfo, this.service.getConversionService(), params, implicitCriteria, SpecificationsBuilder.PARAMS_IGNORE_FOR_CRITERIA);
			// get the page of related children
			Page<M> tmp = this.service.findRelatedPaginated(relatedModelInfo.getModelType(), spec, pageable);
			page = new ParamsAwarePageImpl<M>(params, tmp.getContent(), pageable, tmp.getTotalElements());
		}
		else {
			throw new IllegalArgumentException("Related field info has no reverse field name");
		}
		return page;
	}

	protected ParamsAwarePageImpl<T> findPaginated(Pageable pageable, Map<String, String[]> implicitCriteria) {
		// Get URL query string parameters
		Map<String, String[]> params = request.getParameterMap();

		// Create a JPA query specifications
		Specification<T> spec;
		// Construct the specification manually if no RSQL "filter" param is present
		if (Objects.isNull(params.get("filter"))) {
			spec = this.specificationsBuilder.build(params);
		}
		// else use the RSQL-based specification builder
		else {
			spec = RsqlUtils.buildSpecification(
					this.getModelInfo(),
					this.service.getConversionService(),
					params, implicitCriteria, SpecificationsBuilder.PARAMS_IGNORE_FOR_CRITERIA);
		}
		Page<T> page = this.service.findPaginated(spec, pageable);
		// Return a page with the appropriate meta
		return new ParamsAwarePageImpl<T>(params, page.getContent(), pageable, page.getTotalElements());
	}
}
