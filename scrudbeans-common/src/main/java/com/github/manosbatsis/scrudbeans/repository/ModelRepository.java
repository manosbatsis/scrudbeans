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
package com.github.manosbatsis.scrudbeans.repository;

import com.github.manosbatsis.kotlin.utils.api.Dto;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import java.io.Serializable;
import java.util.Set;

/**
 * Generic repository that provides SCRUD and utility methods based on domain and id type variables.
 *
 * @param <T>  the domain type the repository manages
 * @param <PK> the type of the id of the entity the repository manages
 */
@NoRepositoryBean
public interface ModelRepository<T, PK extends Serializable>
		extends JpaRepository<T, PK>, JpaSpecificationExecutor<T> {

	EntityManager getEntityManager();

	String getIdAttributeName();

	/**
	 * Get the domain type class
	 *
	 * @return the domain type class
	 */
	Class<T> getDomainClass();

	/**
	 * Create a resource.
	 *
	 * @param resource the state to apply
	 * @return resource updated
	 */
	T create(T resource);

	T create(Dto<T> dto);

	/**
	 * Update an existing resource.
	 *
	 * @param resource the state to apply
	 * @return resource updated
	 */
	T update(T resource);

	T update(Dto<T> dto);

	/**
	 * Partially update an existing resource.
	 *
	 * @param delta the patch to apply
	 * @return resource updated
	 */
	T patch(T delta);

	T patch(Dto<T> dto);


//	MetadatumModel addMetadatum(PK subjectId, String predicate, String object);
//
//	List<MetadatumModel> addMetadata(PK subjectId, Map<String, String> metadata);
//
//	void removeMetadatum(PK subjectId, String predicate);
//
//	MetadatumModel findMetadatum(PK subjectId, String predicate);
//
//	/**
//	 * Get the entity's file uploads for this property
//	 * @param subjectId the entity id
//	 * @param propertyName the property holding the upload(s)
//	 * @return the uploads
//	 */
//	List<UploadedFileModel> getUploadsForProperty(PK subjectId, String propertyName);


	PK getIdAttribute(Object o);

	void setIdAttribute(Object o, PK value);

	/**
	 * Validate the given resource using bean validator. {@link javax.persistence.Column} annotations are
	 * also used to validate uniqueness and non-nullable values.
	 *
	 * @param resource
	 * @return the set of failed constraints
	 */
	Set<ConstraintViolation<T>> validateConstraints(T resource);


    /**
     * Get the other end of a ToOne relationship
     *
     * @param id        the id of the root model
     * @param fieldInfo the attribute name of the relationship
     * @return the single entity in the other side of the relation if any, null otherwise
     */
    <RT> RT findRelatedEntityByOwnId(PK id, FieldInfo fieldInfo);

}
