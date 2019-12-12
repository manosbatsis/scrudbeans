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

import static com.github.manosbatsis.scrudbeans.api.util.Mimes.APPLICATION_VND_API_PLUS_JSON_VALUE;
import static com.github.manosbatsis.scrudbeans.api.util.Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.manosbatsis.scrudbeans.api.domain.Persistable;
import com.github.manosbatsis.scrudbeans.api.exception.NotFoundException;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.api.util.HttpUtil;
import com.github.manosbatsis.scrudbeans.domain.RawJson;
import com.github.manosbatsis.scrudbeans.service.PersistableModelService;
import com.github.manosbatsis.scrudbeans.hypermedia.hateoas.ModelResource;
import com.github.manosbatsis.scrudbeans.hypermedia.hateoas.ModelResources;
import com.github.manosbatsis.scrudbeans.hypermedia.hateoas.PagedModelResources;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiDocument;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResourceCollectionDocument;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResourceDocument;
import com.github.manosbatsis.scrudbeans.hypermedia.util.HypermediaUtils;
import com.github.manosbatsis.scrudbeans.rsql.RsqlUtils;
import com.github.manosbatsis.scrudbeans.specification.SpecificationsBuilder;
import com.github.manosbatsis.scrudbeans.uischema.model.UiSchema;
import com.github.manosbatsis.scrudbeans.util.ParamsAwarePageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;


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
public class AbstractPersistableModelController<T extends Persistable<PK>, PK extends Serializable, S extends PersistableModelService<T, PK>>
        extends AbstractModelServiceBackedController<T, PK, S> {

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
	public T plainJsonPost(@RequestBody T resource) {
		return super.create(resource);
	}

	@RequestMapping(
			method = RequestMethod.POST,
			consumes = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE,
			produces = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(description = "Create a new HAL resource")
	public EntityModel<T> hateoasPost(@RequestBody T model) {
		model = super.create(model);
		return toHateoasResource(model);
	}

	@RequestMapping(
			method = RequestMethod.POST,
			consumes = APPLICATION_VND_API_PLUS_JSON_VALUE,
			produces = APPLICATION_VND_API_PLUS_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(description = "Create a new JSON API resource", hidden = true)
	public JsonApiModelResourceDocument<T, PK> jsonApiPost(
			@NonNull @RequestBody JsonApiModelResourceDocument<T, PK> document) {
		// unwrap the submitted model and save
		T model = toModel(document);
		model = super.create(model);
		// repackage and return as a JSON API Document
		return this.toDocument(model);
	}

	// Update
	// =====================
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	@Operation(description = "Update a resource")
	public T plainJsonPut(
			@Parameter(name = "id", required = true)
			@PathVariable PK id, @RequestBody T model) {
		return super.update(id, model);
	}

	@RequestMapping(
			value = "{id}",
			method = RequestMethod.PUT,
			consumes = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE,
			produces = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE)
	@Operation(description = "Update a HAL resource")
	public EntityModel<T> hateoasPut(
			@Parameter(name = "id", required = true)
			@PathVariable PK id, @RequestBody T model) {
		model = super.update(id, model);
		return toHateoasResource(model);
	}

	@RequestMapping(
			value = "{id}",
			method = RequestMethod.PUT,
			consumes = APPLICATION_VND_API_PLUS_JSON_VALUE,
			produces = APPLICATION_VND_API_PLUS_JSON_VALUE)
	@Operation(description = "Update a JSON API Document", hidden = true)
	public JsonApiModelResourceDocument<T, PK> jsonApiPut(
			@Parameter(name = "id", required = true)
			@PathVariable PK id, @RequestBody JsonApiModelResourceDocument<T, PK> document) {
		// unwrap the submitted model and save changes
		T model = toModel(document);
		model = super.update(id, model);
		// repackage and return as a JSON API Document
		return this.toDocument(model);
	}

	// Patch
	// =====================
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	@Operation(
			summary = "Patch (partially update) a resource",
			description = "Partial updates will apply all given properties (ignoring null values) to the persisted entity.")
	public T plainJsonPatch(
			@Parameter(name = "id", required = true)
			@PathVariable PK id, @RequestBody T model) {
		return super.patch(id, model);
	}

	@RequestMapping(
			value = "{id}",
			method = RequestMethod.PATCH,
			consumes = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE,
			produces = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE)
	@Operation(
			summary = "Patch (partially update) a HAL resource",
			description = "Partial updates will apply all given properties (ignoring null values) to the persisted entity.")
	public EntityModel<T> hateoasPatch(
			@Parameter(name = "id", required = true)
			@PathVariable PK id, @RequestBody T model) {
		model = super.patch(id, model);
		return toHateoasResource(model);
	}

	@RequestMapping(
			value = "{id}",
			method = RequestMethod.PATCH,
			consumes = APPLICATION_VND_API_PLUS_JSON_VALUE,
			produces = APPLICATION_VND_API_PLUS_JSON_VALUE)
	@Operation(
			summary = "Patch (partially update) a JSON API Document",
			description = "Partial updates will apply all given properties (ignoring null values) to the persisted entity.",
			hidden = true)
	public JsonApiModelResourceDocument<T, PK> jsonApiPatch(
			@Parameter(name = "id", required = true) @PathVariable PK id,
			@RequestBody JsonApiModelResourceDocument<T, PK> document) {
		// unwrap the submitted model and save changes
		T model = toModel(document);
		model = super.patch(id, model);
		// repackage and return as a JSON API Document
		return this.toDocument(model);
	}

	// Find all (no paging)
	// ========================
	@RequestMapping(method = RequestMethod.GET, params = "page=no")
	@Operation(
			summary = "Get the full collection of resources (no paging or criteria)",
			description = "Find all resources, and return the full collection (i.e. VS a page of the total results)")
	public Iterable<T> plainJsonGetAll() {
		return super.findAll();
	}

	@RequestMapping(
			method = RequestMethod.GET,
			params = "page=no",
			consumes = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE,
			produces = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE)
	@Operation(
			summary = "Get the full collection of HAL resources (no paging or criteria)",
			description = "Find all HAL resources, and return the full collection (i.e. VS a page of the total results)")
	public ModelResources<T> hateoasGetAll() {
		return toHateoasResources(super.findAll());
	}

	@RequestMapping(
			method = RequestMethod.GET,
			params = "page=no",
			consumes = APPLICATION_VND_API_PLUS_JSON_VALUE,
			produces = APPLICATION_VND_API_PLUS_JSON_VALUE)
	@Operation(
			summary = "Get the full collection of JSON API Documents (no paging or criteria)",
			description = "Find all JSON API Documents, and return the full collection (i.e. VS a page of the total results)",
			hidden = true)
	public JsonApiModelResourceCollectionDocument jsonApiGetAll() {
		// obtain result models
		Iterable<T> models = super.findAll();
		// repackage and return as a JSON API Document
		return this.toDocument(models);
	}


	// Search
	// ========================

	//@Override
	@RequestMapping(method = RequestMethod.GET)
	@Operation(summary = "Search for resources (paginated).", description = "Find all resources matching the given criteria and return a paginated collection."
			+ "Predefined paging properties are _pn (page number), _ps (page size) and sort. All serialized member names "
			+ "of the resource are supported as search criteria in the form of HTTP URL parameters.")
	public ParamsAwarePageImpl<T> plainJsonGetPage(
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

	//@Override
	@RequestMapping(
			method = RequestMethod.GET,
			consumes = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE,
			produces = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE)
	@Operation(
			summary = "Search for resources (paginated).",
			description = "Find all resources matching the given criteria and return a paginated collection."
					+ "Predefined paging properties are _pn (page number), _ps (page size) and sort. All serialized member names "
					+ "of the resource are supported as search criteria in the form of HTTP URL parameters.")
	public PagedModelResources<T> hateoasGetPage(
			@Parameter(name = SpecificationsBuilder.PARAM_FILTER, description = "The RSQL/FIQL query to use. Simply URL param based search will be used if missing.")
			@RequestParam(value = SpecificationsBuilder.PARAM_FILTER, required = false) String filter,
			@Parameter(name = SpecificationsBuilder.PARAM_PAGE_NUMBER, description = "The page number")
			@RequestParam(value = SpecificationsBuilder.PARAM_PAGE_NUMBER, required = false, defaultValue = "0") Integer page,
			@Parameter(name = SpecificationsBuilder.PARAM_PAGE_SIZE, description = "The page size")
			@RequestParam(value = SpecificationsBuilder.PARAM_PAGE_SIZE, required = false, defaultValue = "10") Integer size,
			@Parameter(name = SpecificationsBuilder.PARAM_SORT, description = "Comma separated list of attribute names, descending for each one prefixed with a dash, ascending otherwise")
			@RequestParam(value = SpecificationsBuilder.PARAM_SORT, required = false, defaultValue = "id") String sort) {
		Pageable pageable = PageableUtil.buildPageable(page, size, sort);
		return this.toHateoasPagedResources(
				this.<T>findPaginated(pageable, null), "_pn");
	}

	@RequestMapping(
			method = RequestMethod.GET,
			consumes = APPLICATION_VND_API_PLUS_JSON_VALUE,
			produces = APPLICATION_VND_API_PLUS_JSON_VALUE)
	@Operation(
			summary = "Search for resources (paginated).",
			description = "Find all resources matching the given criteria and return a paginated JSON API Document.",
			hidden = true)
	public JsonApiModelResourceCollectionDocument<T, PK> jsonApiGetPage(
			@Parameter(name = SpecificationsBuilder.PARAM_FILTER, description = "The RSQL/FIQL query to use. Simply URL param based search will be used if missing.")
			@RequestParam(value = SpecificationsBuilder.PARAM_FILTER, required = false) String filter,
			@Parameter(name = SpecificationsBuilder.PARAM_JSONAPI_PAGE_NUMBER, description = "The page number")
			@RequestParam(value = SpecificationsBuilder.PARAM_JSONAPI_PAGE_NUMBER, required = false, defaultValue = "0") Integer page,
			@Parameter(name = SpecificationsBuilder.PARAM_JSONAPI_PAGE_SIZE, description = "The page size")
			@RequestParam(value = SpecificationsBuilder.PARAM_JSONAPI_PAGE_SIZE, required = false, defaultValue = "10") Integer size,
			@Parameter(name = SpecificationsBuilder.PARAM_SORT, description = "Comma separated list of attribute names, descending for each one prefixed with a dash, ascending otherwise")
			@RequestParam(value = SpecificationsBuilder.PARAM_SORT, required = false, defaultValue = "id") String sort) {
		Pageable pageable = PageableUtil.buildPageable(page, size, sort);
		return toPageDocument(this.<T>findPaginated(pageable, null));
	}

	// Read
	// ==============
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	@Operation(summary = "Find by id", description = "Find a resource by it's identifier")
	public T plainJsonGetById(@Parameter(name = "id", required = true) @PathVariable PK id) {
		T model = super.findById(id);
		if (model == null) {
			throw new NotFoundException();
		}
		return model;
	}

	@RequestMapping(value = "{id}",
			method = RequestMethod.GET,
			consumes = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE,
			produces = MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE)
	@Operation(summary = "Find by id", description = "Find a resource by it's identifier")
	public ModelResource<T> hateoasGetById(@Parameter(name = "id", required = true) @PathVariable PK id) {
		LOGGER.debug("plainJsonGetById, id: {}, model type: {}", id, this.service.getDomainClass());
		T model = super.findById(id);
		if (model == null) {
			throw new NotFoundException();
		}
		return toHateoasResource(model);
	}

	@RequestMapping(value = "{id}",
			method = RequestMethod.GET,
			consumes = APPLICATION_VND_API_PLUS_JSON_VALUE,
			produces = APPLICATION_VND_API_PLUS_JSON_VALUE)
	@Operation(summary = "Find by id", description = "Find a resource by it's identifier", hidden = true)
	public JsonApiModelResourceDocument<T, PK> jsonApiGetById(@Parameter(name = "id", required = true) @PathVariable PK id) {
		return toDocument(super.findById(id));
	}

	/**
	 * GET has the same effect to both member and relationship endpoints
	 */
	@RequestMapping(value = {"{id}/{relationName}", "{id}/relationships/{relationName}"}, method = RequestMethod.GET)
	@Operation(summary = "Find related by root id", description = "Find the related resource for the given relation name and identifier")
	public ResponseEntity plainJsonGetRelated(
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
            Persistable related = this.findRelatedSingle(id, fieldInfo);
            // if found
			EntityModel res = HypermediaUtils.toHateoasResource(related, fieldInfo.getRelatedModelInfo());
            responseEntity = new ResponseEntity(res, HttpStatus.OK);
        }
		else if (fieldInfo.isOneToMany()) {
			Pageable pageable = PageableUtil.buildPageable(page, size, sort);
			ParamsAwarePageImpl resultsPage = this.findRelatedPaginated(id, pageable, fieldInfo);
			PagedModel resources = this.toHateoasPagedResources(resultsPage, "_pn");
			responseEntity = new ResponseEntity(resources, HttpStatus.OK);

		}


		return responseEntity;
	}

	/**
	 * GET has the same effect to both member and relationship endpoints
	 */
	@RequestMapping(value = {"{id}/{relationName}", "{id}/relationships/{relationName}"}, method = RequestMethod.GET, consumes = APPLICATION_VND_API_PLUS_JSON_VALUE, produces = APPLICATION_VND_API_PLUS_JSON_VALUE)
	@Operation(summary = "Find related by root id",
			description = "Find the related resource for the given relation name and identifier",
			hidden = true)
	public JsonApiDocument jsonApiGetRelated(
			@Parameter(name = SpecificationsBuilder.PARAM_PK, required = true) @PathVariable PK id,
			@Parameter(name = SpecificationsBuilder.PARAM_RELATION_NAME, required = true) @PathVariable String relationName,

			@Parameter(name = SpecificationsBuilder.PARAM_FILTER, description = "The RSQL/FIQL query to use. Simply URL param based search will be used if missing.")
			@RequestParam(value = SpecificationsBuilder.PARAM_FILTER, required = false) String filter,

			@Parameter(name = SpecificationsBuilder.PARAM_JSONAPI_PAGE_NUMBER, description = "The page number")
			@RequestParam(value = SpecificationsBuilder.PARAM_JSONAPI_PAGE_NUMBER, required = false, defaultValue = "0") Integer page,

			@Parameter(name = SpecificationsBuilder.PARAM_JSONAPI_PAGE_SIZE, description = "The page size")
			@RequestParam(value = SpecificationsBuilder.PARAM_JSONAPI_PAGE_SIZE, required = false, defaultValue = "10") Integer size,
			@Parameter(name = SpecificationsBuilder.PARAM_SORT,
					description = "Comma separated list of attribute names, descending for each one prefixed with a dash, ascending otherwise")
			@RequestParam(name = SpecificationsBuilder.PARAM_SORT, required = false) String sort) {

		// get the field info for the relation, if any
		FieldInfo fieldInfo = this.getModelInfo().getField(relationName);

		// throw error if not valid or linkable relationship
		if (fieldInfo == null || !fieldInfo.isLinkableResource()) {
			throw new IllegalArgumentException("Invalid relationship: " + relationName);
		}

		// use JSON API Document to accommodate different return types
		JsonApiDocument document = null;

		// if ToOne
		if (fieldInfo.isToOne()) {
            Persistable related = this.findRelatedSingle(id, fieldInfo);
            // if found
            if (related != null) {
                document = HypermediaUtils.toDocument(related, fieldInfo.getRelatedModelInfo());
            }
        }
		else if (fieldInfo.isOneToMany()) {
			Pageable pageable = PageableUtil.buildPageable(page, size, sort);
			ParamsAwarePageImpl resultsPage = this.findRelatedPaginated(id, pageable, fieldInfo);
			document = this.toPageDocument(resultsPage, fieldInfo.getRelatedModelInfo(), "page[number]");

		}


		return document;
	}


	@RequestMapping(params = "ids", method = RequestMethod.GET)
	@Operation(summary = "Search by ids", description = "Find the set of resources matching the given identifiers.")
	public ModelResources<T> plainJsonGetByIds(@RequestParam(value = "ids[]") Set<PK> ids) {
		return this.toHateoasResources(super.findByIds(ids));
	}

	@RequestMapping(params = "ids", method = RequestMethod.GET, consumes = APPLICATION_VND_API_PLUS_JSON_VALUE, produces = APPLICATION_VND_API_PLUS_JSON_VALUE)
	@Operation(summary = "Search by ids", description = "Find the set of resources matching the given identifiers.")
	public JsonApiModelResourceCollectionDocument<T, PK> jsonApiGetByIds(@RequestParam(value = "ids[]") Set<PK> ids) {
		return toDocument(super.findByIds(ids));
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete a resource", description = "Delete a resource by its identifier. ", method = "DELETE")
	public void plainJsonDelete(@Parameter(name = "id", required = true) @PathVariable PK id) {
		super.delete(id);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE, consumes = APPLICATION_VND_API_PLUS_JSON_VALUE, produces = APPLICATION_VND_API_PLUS_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete a resource",
			description = "Delete a resource by its identifier. ",
			method = "DELETE",
			hidden = true)
	public void jsonApiDelete(@Parameter(name = "id", required = true) @PathVariable PK id) {
		super.delete(id);
	}

	@RequestMapping(value = "jsonschema", method = RequestMethod.GET, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get JSON Schema", description = "Get the JSON Schema for the controller entity type")
	public RawJson plainJsonGetJsonSchema() throws JsonProcessingException {
		return super.getJsonSchema();
	}

	@RequestMapping(value = "uischema", method = RequestMethod.GET, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get UI schema", description = "Get the UI achema for the controller entity type, including fields, use-cases etc.")
	@Deprecated
	public UiSchema plainJsonGetUiSchema() {
		return super.getUiSchema();
	}

	@RequestMapping(method = RequestMethod.OPTIONS, consumes = {APPLICATION_VND_API_PLUS_JSON_VALUE, MimeTypeUtils.APPLICATION_JSON_VALUE}, produces = {APPLICATION_VND_API_PLUS_JSON_VALUE, MimeTypeUtils.APPLICATION_JSON_VALUE})
	@Operation(summary = "Get CORS headers", description = "Get the CORS headers for the given path")
	public void options(HttpServletResponse response) {
		response.setHeader(HttpUtil.ACESS_CONTROL_CREDENTIALS_NAME, "true");
		response.setHeader(HttpUtil.ACESS_CONTROL_ORIGIN_NAME, "http://localhost:9000");
		response.setHeader(HttpUtil.ACESS_CONTROL_METHODS_NAME, "GET, OPTIONS, POST, PUT, DELETE");
		response.setHeader(HttpUtil.ACESS_CONTROL_HEADERS_NAME, "Origin, X-Requested-With, Content-Type, Accept");
		response.setHeader(HttpUtil.ACESS_CONTROL_MAX_AGE_NAME, "3600");

	}


    /**
     * Find the other end of a ToOne relationship
     *
     * @param id        the root entity ID
     * @param fieldInfo the member/relation name
     * @return the single related entity, if any
     * @see PersistableModelService#findRelatedSingle(Serializable, FieldInfo)
     */
    protected Persistable findRelatedSingle(PK id, FieldInfo fieldInfo) {
        Persistable resource = this.service.findRelatedSingle(id, fieldInfo);
        return resource;
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
    protected <M extends Persistable> ParamsAwarePageImpl<M> findRelatedPaginated(PK id, Pageable pageable, FieldInfo fieldInfo) {
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
