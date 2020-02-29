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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.manosbatsis.kotlin.utils.api.Dto;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfoRegistry;
import com.github.manosbatsis.scrudbeans.api.mdd.service.ModelService;
import com.github.manosbatsis.scrudbeans.domain.RawJson;
import com.github.manosbatsis.scrudbeans.uischema.model.UiSchema;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Set;


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
public class AbstractModelServiceBackedController<
        T,
        PK extends Serializable,
        S extends ModelService<T, PK>,
        DTO extends Dto<T>> implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelServiceBackedController.class);

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
        this.isResourceSupport = RepresentationModel.class.isAssignableFrom(this.modelType);
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

	protected T create(@NonNull T resource) {
		applyCurrentPrincipal(resource);
		return this.service.create(resource);
	}

	protected T create(@NonNull DTO resource) {
		applyCurrentPrincipal(resource);
		return this.service.create(resource);
	}

	protected T update(@NonNull PK id, @NonNull T resource) {
		service.setIdAttribute(resource, id);
		applyCurrentPrincipal(resource);
		resource = this.service.update(resource);
		return resource;
	}

	protected T update(@NonNull PK id, @NonNull DTO resource) {
		service.setIdAttribute(resource, id);
		applyCurrentPrincipal(resource);
		return this.service.update(resource);
	}

	protected T patch(@NonNull PK id, @NonNull T resource) {
		service.setIdAttribute(resource, id);
		applyCurrentPrincipal(resource);
		resource = this.service.patch(resource);
		return resource;
	}

	protected T patch(@NonNull PK id, @NonNull DTO resource) {
		service.setIdAttribute(resource, id);
		applyCurrentPrincipal(resource);
		return this.service.patch(resource);
	}

	protected Iterable<T> findAll() {
		return service.findAll();
	}

	protected T findById(@NonNull PK id) {
		LOGGER.debug("plainJsonGetById, id: {}, model type: {}", id, this.service.getDomainClass());
		T resource = this.service.findById(id);
		return resource;
	}


	protected Iterable<T> findByIds(@NonNull Set<PK> ids) {
		Assert.notNull(ids, "ids list cannot be null");
		return this.service.findByIds(ids);
	}


	protected void delete(@NonNull PK id) {
		this.service.delete(this.findById(id));
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

	protected void applyCurrentPrincipal(Object resource) {
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
