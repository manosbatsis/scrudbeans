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
package com.github.manosbatsis.scrudbeans.jpa.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.manosbatsis.scrudbeans.api.domain.PersistableModel;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfoRegistry;
import com.github.manosbatsis.scrudbeans.api.mdd.service.ModelService;
import com.github.manosbatsis.scrudbeans.api.util.ParamsAwarePage;
import com.github.manosbatsis.scrudbeans.common.domain.RawJson;
import com.github.manosbatsis.scrudbeans.hypermedia.hateoas.ModelResource;
import com.github.manosbatsis.scrudbeans.hypermedia.hateoas.ModelResources;
import com.github.manosbatsis.scrudbeans.hypermedia.hateoas.PagedModelResources;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResource;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResourceCollectionDocument;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResourceDocument;
import com.github.manosbatsis.scrudbeans.hypermedia.util.HypermediaUtils;
import com.github.manosbatsis.scrudbeans.hypermedia.util.JsonApiModelBasedDocumentBuilder;
import com.github.manosbatsis.scrudbeans.jpa.uischema.model.UiSchema;
import com.github.manosbatsis.scrudbeans.jpa.util.ParamsAwarePageImpl;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;


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
 * @param <PK> Resource id type, usually Long or String
 * @param <S>  The service class
 */
public class AbstractModelServiceBackedController<T extends PersistableModel<PK>, PK extends Serializable, S extends ModelService<T, PK>> implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelServiceBackedController.class);

	protected static final String PARAM_RELATION_NAME = "relationName";

	protected static final String PARAM_FILTER = "filter";

	protected static final String PARAM_JSONAPI_PAGE_NUMBER = "page[number]";

	protected static final String PARAM_JSONAPI_PAGE_SIZE = "page[size]";

	protected static final String PARAM_SORT = "sort";

	protected static final String PARAM_PK = "id";

	protected static final String PARAM_PAGE_NUMBER = "_pn";

	protected static final String PARAM_PAGE_SIZE = "_ps";

	protected static final String[] PARAMS_IGNORE_FOR_CRITERIA = {PARAM_RELATION_NAME, PARAM_FILTER, PARAM_JSONAPI_PAGE_NUMBER, PARAM_JSONAPI_PAGE_SIZE, PARAM_SORT, PARAM_PK, PARAM_PAGE_NUMBER, PARAM_PAGE_SIZE};

	private ModelInfo modelInfo;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	protected HttpServletRequest request;

	@Autowired
	protected ModelInfoRegistry mmdelInfoRegistry;

	//@Autowired
	//protected EntityLinks entityLinks;

	protected S service;

	protected Class<T> modelType;

	protected Boolean isResourceSupport = false;


	@Autowired//@Inject
	public void setService(S service) {
		this.service = service;
	}


	public S getService() {
		return this.service;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.modelType = this.service.getDomainClass();
		this.isResourceSupport = ResourceSupport.class.isAssignableFrom(this.modelType);
	}

	/**
	 * Get the ModelInfo for this Controller's Model type
	 */
	protected ModelInfo getModelInfo() {
		if (this.modelInfo == null) {
			this.modelInfo = this.mmdelInfoRegistry.getEntryFor(this.modelType);
			LOGGER.debug("toHateoasResource, modelInfo: {}", this.modelInfo);
		}
		return this.modelInfo;
	}


	/**
	 * Wrap the given model in a {@link Resource} and add {@link Link}s
	 *
	 * @param model
	 */
	protected ModelResource<T> toHateoasResource(@NonNull T model) {
		return HypermediaUtils.toHateoasResource(model, this.mmdelInfoRegistry.getEntryFor(model.getClass()));
	}


	/**
	 * Wrap the given models in a {@link Resources} and add {@link Link}s
	 *
	 * @param models
	 */
	protected ModelResources<T> toHateoasResources(@NonNull Iterable<T> models) {
		Class<T> modelType = this.modelType;
		return HypermediaUtils.toHateoasResources(models, modelType, this.mmdelInfoRegistry);
	}

	/**
	 * Convert the given {@link Page} to a {@link PagedResources} object and add {@link Link}s
	 *
	 * @param page
	 */
	protected PagedModelResources<T> toHateoasPagedResources(@NonNull ParamsAwarePageImpl<T> page, @NonNull String pageNumberParamName) {
		Class<T> modelType = this.modelType;
		return toHateoasPagedResources(page, modelType, pageNumberParamName);
	}

	/**
	 * Convert the given {@link Page} to a {@link PagedResources} object and add {@link Link}s
	 *
	 * @param page
	 */
	protected <RT extends PersistableModel> PagedModelResources<RT> toHateoasPagedResources(@NonNull ParamsAwarePage<RT> page, @NonNull Class<RT> modelType, @NonNull String pageNumberParamName) {
		ModelInfo rootModelInfo = this.mmdelInfoRegistry.getEntryFor(modelType);

		// long size, long number, long totalElements, long totalPages
		PagedModelResources<RT> pagedResources = HypermediaUtils.toHateoasPagedResources(page, request, pageNumberParamName, this.mmdelInfoRegistry);


		return pagedResources;
	}

	/**
	 * Wrap the given model in a JSON API Document
	 * @param model the model to wrap
	 */
	protected JsonApiModelResourceDocument<T, PK> toDocument(T model) {
		return HypermediaUtils.toDocument(model, this.getModelInfo());
	}


	/**
	 * Wrap the given collection of models in a JSON API Document
	 * @param models the models to wrap
	 */
	protected JsonApiModelResourceCollectionDocument<T, PK> toDocument(Collection<T> models) {
		return new JsonApiModelBasedDocumentBuilder<T, PK>(this.getModelInfo().getUriComponent())
				.withData(models)
				.buildModelCollectionDocument();
	}

	/**
	 * Wrap the given iterable of models of models in a JSON API Document
	 * @param models the models to wrap
	 */
	protected JsonApiModelResourceCollectionDocument<T, PK> toDocument(Iterable<T> models) {
		return new JsonApiModelBasedDocumentBuilder<T, PK>(this.getModelInfo().getUriComponent())
				.withData(models)
				.buildModelCollectionDocument();
	}

	/**
	 * Wrap the given {@link Page} of models in a JSON API Document
	 * @param page the page to wrap
	 */
	protected JsonApiModelResourceCollectionDocument<T, PK> toPageDocument(ParamsAwarePage<T> page) {

		Class<T> modelType = this.modelType;
		return toPageDocument(page, this.getModelInfo(), "page[number]");
	}

	/*
	 * Wrap the given {@link Page} of models in a JSON API Document
	 * @param page the page to wrap
	 * @param modelInfo
	 * @param pageNumberParamName
	 * @param <RT>
	 * @param <RPK>
	 */
	protected <RT extends PersistableModel<RPK>, RPK extends Serializable> JsonApiModelResourceCollectionDocument<RT, RPK> toPageDocument(@NonNull ParamsAwarePage<RT> page, @NonNull ModelInfo<RT, RPK> modelInfo, @NonNull String pageNumberParamName) {
		JsonApiModelResourceCollectionDocument<RT, RPK> doc = new JsonApiModelBasedDocumentBuilder<RT, RPK>(this.getModelInfo().getUriComponent())
				.withData(page)
				.buildModelCollectionDocument();
		List<Link> tmp = HypermediaUtils.buileHateoasLinks(page, request, pageNumberParamName);
		LOGGER.debug("toPageDocument, pageLinks: {}", tmp);
		if (CollectionUtils.isNotEmpty(tmp)) {
			for (Link l : tmp) {
				doc.add(l.getRel(), l.getHref());
			}
		}
		return doc;
	}

	/**
	 * Unwrap the single model given as a JSON API Document
	 */
	protected T toModel(@NonNull @RequestBody JsonApiModelResourceDocument<T, PK> document) {
		T entity = null;
		JsonApiModelResource<T, PK> resource = document.getData();
		if (resource != null) {
			entity = resource.getAttributes();
			entity.setId(resource.getIdentifier());
		}
		return entity;
	}


	protected T create(@NonNull T resource) {
		applyCurrentPrincipal(resource);
		return this.service.create(resource);
	}


	protected T update(PK id, T resource) {
		Assert.notNull(id, "id cannot be null");
		resource.setId(id);
		applyCurrentPrincipal(resource);
		resource = this.service.update(resource);
		return resource;
	}


	protected T patch(PK id, T resource) {
		applyCurrentPrincipal(resource);
		resource.setId(id);
		resource = this.service.patch(resource);
		return resource;
	}


	protected Iterable<T> findAll() {
		return service.findAll();
	}

	protected T findById(PK id) {
		LOGGER.debug("plainJsonGetById, id: {}, model type: {}", id, this.service.getDomainClass());
		T resource = this.service.findById(id);
		return resource;
	}


	protected Iterable<T> findByIds(@NonNull Set<PK> ids) {
		Assert.notNull(ids, "ids list cannot be null");
		return this.service.findByIds(ids);
	}


	protected void delete(@NonNull PK id) {
		T resource = this.findById(id);
		this.service.delete(resource);
	}


	protected void deleteAll() {
		LOGGER.warn("deleteAll: no-op");
	}

	protected RawJson getJsonSchema() throws JsonProcessingException {
		JsonSchemaConfig config = JsonSchemaConfig.nullableJsonSchemaDraft4();
		JsonSchemaGenerator generator = new JsonSchemaGenerator(objectMapper, config);

		JsonNode jsonSchema = generator.generateJsonSchema(this.getService().getDomainClass());

		String jsonSchemaAsString = objectMapper.writeValueAsString(jsonSchema);
		return new RawJson(jsonSchemaAsString);
	}

	protected UiSchema getUiSchema() {
		UiSchema schema = new UiSchema(this.service.getDomainClass());
		return schema;
	}





	protected void applyCurrentPrincipal(T resource) {
		// TODO
//        Field[] fields = FieldUtils.getFieldsWithAnnotation(this.service.getDomainClass(), CurrentPrincipal.class);
//        //ApplyPrincipalUse predicate = this.service.getDomainClass().getAnnotation(CurrentPrincipalField.class);
//        if (fields.length > 0) {
//            UserDetails principal = this.service.getPrincipal();
//            for (int i = 0; i < fields.length; i++) {
//                Field field = fields[i];
//                CurrentPrincipal applyRule = field.getAnnotation(CurrentPrincipal.class);
//
//                // if property is not already set
//                try {
//                    if (PropertyUtils.getProperty(resource, field.getName()) == null) {
//                        boolean skipApply = this.hasAnyRole(applyRule.ignoreforRoles());
//                        // if role is not ignored
//                        if (!skipApply) {
//                            String id = principal != null ? principal.getId() : null;
//                            if (id != null) {
//                                User user = new User();
//                                user.setId(id);
//                                LOGGER.debug("Applying principal to field: {}, pathFragment: {}", id, field.getName());
//                                PropertyUtils.setProperty(resource, field.getName(), user);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException("Failed to apply CurrentPrincipal annotation", e);
//                }
//
//            }
//
//
//        }
	}
}
