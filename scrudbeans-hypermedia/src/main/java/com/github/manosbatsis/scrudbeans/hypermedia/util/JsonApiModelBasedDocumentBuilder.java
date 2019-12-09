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
package com.github.manosbatsis.scrudbeans.hypermedia.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.manosbatsis.scrudbeans.api.domain.Persistable;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiDocument;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiLink;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResource;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResourceCollectionDocument;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResourceDocument;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.support.SimpleModelResource;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.support.SimpleModelResourceCollectionDocument;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.support.SimpleModelResourceDocument;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

/**
 * The concrete implementation build and returned using this class is either a  {@link SimpleModelResourceDocument} or {@link SimpleModelResourceCollectionDocument}
 * depending on whether a single or a collection of resources were provided using one of the <code>withData</code> methods, some of which also automatically add
 * metadata to the resulting document. Only a single call of <code>withData</code> or <code>withErrors</code> method signatures is allowed.
 *
 * <h>Example: create a Document for a single model resource:</h>
 * <pre>
 * {@code
 * // create a document with a single Country model
 * JsonApiModelDocument<Country, String> doc = new JsonApiModelBasedDocumentBuilder<>("countries")
 *      .withData(countryModel)
 *      .buildModelDocument(); // or just .build() to get a JsonApiDocument
 * }
 * </pre>
 *
 * <h>Example: create a Document with multiple model resources:</h>
 * <pre>
 * {@code
 * // create a document for multiple Country models
 * JsonApiModelCollectionDocument doc = new JsonApiModelBasedDocumentBuilder<T, PK>("countries")
 *      .withData(countryModels)
 *      .buildModelCollectionDocument(); // or just .build() to get a JsonApiDocument
 * }
 * </pre>
 *
 *
 */
public class JsonApiModelBasedDocumentBuilder<T extends Persistable<PK>, PK extends Serializable> {


    private String jsonType;

    private Collection<Error> errors;

    private Map<String, Serializable> meta;

    private Collection<JsonApiModelResource> included;

	private List<JsonApiLink> links;

	private SimpleModelResource<T, PK> resource;

	private List<SimpleModelResource<T, PK>> resources;

	// TODO: add config options for links, included
	public JsonApiModelBasedDocumentBuilder() {
	}

	public JsonApiModelBasedDocumentBuilder(String jsonType) {
		this.jsonType = jsonType;
	}

	/**
	 * Add the given collection of errors to the result document
	 * @param errors the errors to add
	 * @return
	 * @throws IllegalStateException if data have already been set
	 */
	public JsonApiModelBasedDocumentBuilder withErrors(Collection<Error> errors) {
		if (this.resource != null || CollectionUtils.isNotEmpty(this.resources)) {
			throw new IllegalStateException("Cannot include both errors and resources in the same document");
		}
		if (this.errors != null) {
			throw new IllegalStateException("Cannot set errors as they have already been set");
		}
		this.errors = errors;
		return this;
	}


	public JsonApiModelBasedDocumentBuilder withIncluded(Collection<JsonApiModelResource> included) {
		this.included = included;
		return this;
	}

	public JsonApiModelBasedDocumentBuilder addLinks(Collection<JsonApiLink> links) {
		if (this.links == null) {
			this.links = new LinkedList<>();
		}
		this.links.addAll(links);
		return this;
	}

	/**
	 * Add the given <code>meta</code> entries to the document
	 * @param meta
	 * @return
	 */
	public JsonApiModelBasedDocumentBuilder addMeta(Map<String, Serializable> meta) {
		if (this.meta == null) {
			this.meta = new HashMap<>();
		}
		this.meta.putAll(meta);
		return this;
	}

	/**
	 * Add the given <code>meta</code> entry to the document
	 * @param name
	 * @param value
	 */
	public void addMeta(String name, Serializable value) {
		if (this.meta == null) {
			this.meta = new HashMap<>();
		}
		this.meta.put(name, value);

	}

	/**
	 * Sets the document <code>data</code> using the page contents and adds the  following <code>meta</code> entries:
	 * <ul>
	 *     <li>(boolean) first</li>
	 *     <li>(boolean) last</li>
	 *     <li>(int) size</li>
	 *     <li>(int) number</li>
	 *     <li>(int) numberOfElements</li>
	 *     <li>(int) totalElements</li>
	 *     <li>(int) totalPages</li>
	 *     <li>({@link Sort}) sort</li>
	 * </ul>
	 * @param page
	 * @return
	 * @throws IllegalStateException if data or errors have already been set
	 */
	public JsonApiModelBasedDocumentBuilder withData(@NonNull Page<T> page) {
		this.withData(page.getContent());

		this.addMeta("first", page.isFirst());
		this.addMeta("last", page.isLast());
		this.addMeta("size", page.getSize());
		this.addMeta("number", page.getNumber());
		this.addMeta("numberOfElements", page.getNumberOfElements());
		this.addMeta("totalElements", page.getTotalElements());
		this.addMeta("totalPages", page.getTotalPages());
		this.addMeta("sort", page.getSort());

		return this;
	}

	/**
	 * Sets the document <code>data</code> using the given models {@link Collection} and adds the following <code>meta</code> entries:
	 * <ul>
	 *     <li>(boolean) first</li>
	 *     <li>(boolean) last</li>
	 *     <li>(int) size</li>
	 *     <li>(int, always 0) number</li>
	 *     <li>(int) numberOfElements</li>
	 *     <li>(int) totalElements</li>
	 *     <li>(int, always 1) totalPages</li>
	 *     <li>({@link Sort}) sort</li>
	 * </ul>
	 * @param models
	 * @return
	 * @throws IllegalStateException if data or errors have already been set
	 */
	public JsonApiModelBasedDocumentBuilder withData(@NonNull Collection<T> models) {

		// validate state
		ensureStateCanAcceptMultipleResources();
		if (CollectionUtils.isNotEmpty(models) && CollectionUtils.isNotEmpty(this.errors)) {
			throw new IllegalStateException("Was given a non-empty collection of models but errors are already set");
		}

		// apply
		this.resources = new ArrayList(models.size());
		for (T model : models) {
			this.resources.add(new SimpleModelResource<>(model, this.jsonType));
		}

		// update meta
		addDefaultMetaForResourcesSet();

		return this;
	}

	/**
	 * Sets the document <code>data</code> using the given models {@link Iterable} and adds the following <code>meta</code> entries:
	 * <ul>
	 *     <li>(boolean) first</li>
	 *     <li>(boolean) last</li>
	 *     <li>(int) size</li>
	 *     <li>(int, always 0) number</li>
	 *     <li>(int) numberOfElements</li>
	 *     <li>(int) totalElements</li>
	 *     <li>(int, always 1) totalPages</li>
	 *     <li>({@link Sort}) sort</li>
	 * </ul>
	 * @param models
	 * @return
	 * @throws IllegalStateException if data or errors have already been set
	 */
	public JsonApiModelBasedDocumentBuilder withData(@NonNull Iterable<T> models) {

		// validate state
		ensureStateCanAcceptMultipleResources();

		// apply
		this.resources = new LinkedList();
		for (T model : models) {
			this.resources.add(new SimpleModelResource<>(model, this.jsonType));
		}

		// update meta
		addDefaultMetaForResourcesSet();

		return this;
	}


	/**
	 * Sets the document <code>data</code> using the given resources and adds the following <code>meta</code> entries:
	 * <ul>
	 *     <li>(boolean) first</li>
	 *     <li>(boolean) last</li>
	 *     <li>(int) size</li>
	 *     <li>(int, always 0) number</li>
	 *     <li>(int) numberOfElements</li>
	 *     <li>(int) totalElements</li>
	 *     <li>(int, always 1) totalPages</li>
	 *     <li>({@link Sort}) sort</li>
	 * </ul>
	 * @param resources
	 * @return
	 * @throws IllegalStateException if data or errors have already been set
	 */
	public JsonApiModelBasedDocumentBuilder withData(SimpleModelResource<T, PK>... resources) {

		// validate state
		ensureStateCanAcceptMultipleResources();
		if (ArrayUtils.isNotEmpty(resources) && CollectionUtils.isNotEmpty(this.errors)) {
			throw new IllegalStateException("Was given a non-empty collection of resources but errors are already set");
		}

		this.resources = new ArrayList(resources.length);
		for (SimpleModelResource<T, PK> rs : resources) {
			this.resources.add(rs);
		}

		// update meta
		addDefaultMetaForResourcesSet();

		return this;
	}

	/**
	 * Sets the document <code>data</code> as a single resource using the given model.
	 * @param model
	 * @return
	 * @throws IllegalStateException if data or errors have already been set
	 */
	public JsonApiModelBasedDocumentBuilder withData(T model) {

		// validate state
		ensureStateCanAcceptSingleResource();

		// set and return
		this.resource = new SimpleModelResource<>(model, this.jsonType);
		return this;
	}

	/**
	 * Sets the document <code>data</code> as a single resource
	 * @param resource
	 * @return
	 * @throws IllegalStateException if data or errors have already been set
	 */
	public JsonApiModelBasedDocumentBuilder withData(SimpleModelResource<T, PK> resource) {

		// validate state
		ensureStateCanAcceptSingleResource();

		// set and return
		this.resource = resource;
		return this;
	}


	/**
	 * Build and return  a {@link SimpleModelResourceDocument} or {@link SimpleModelResourceCollectionDocument} instance
	 * depending on whether a single or a collection of resources was provided using one of the <code>withData</code> methods.
	 *
	 * To avoid casting when controlling the result use {@link #buildModelDocument()} or {@link #buildModelCollectionDocument()}
	 *
	 * @see JsonApiModelBasedDocumentBuilder#buildModelDocument()
	 * @see JsonApiModelBasedDocumentBuilder#buildModelCollectionDocument()
	 * @return
	 */
	public JsonApiDocument build() {
		return build(true);
	}

	/**
	 * Build and return  a {@link SimpleModelResourceDocument} or {@link SimpleModelResourceCollectionDocument} instance
	 * depending on whether a single or a collection of resources was provided using one of the <code>withData</code> methods.
	 *
	 * In case of errors, the first or the second document type will be used based on <code>errorTypeSingle</code>
	 * @param errorTypeSingle whether to use a SimpleModelDocument in case opf errors
	 * @return
	 */
	protected JsonApiDocument build(boolean errorTypeSingle) {
		JsonApiDocument document;
		if (this.errors != null) {
			document = errorTypeSingle ? new SimpleModelResourceDocument() : new SimpleModelResourceCollectionDocument();
			// TODO
			//document.setErrors(this.errors);
		}
		else if (this.resource != null) {
			document = new SimpleModelResourceDocument(this.resource);
		}
		else if (this.resources != null) {
			document = new SimpleModelResourceCollectionDocument(this.resources);
		}
		else {
			throw new IllegalStateException("Cannot build a JsonApiDocument without a single resource, resource collection or errors");
		}

		if (CollectionUtils.isNotEmpty(this.links)) {
			document.add(this.links);
		}
		if (CollectionUtils.isNotEmpty(this.included)) {
			document.setIncluded(this.included);
		}
		if (MapUtils.isNotEmpty(this.meta)) {
			document.setMeta(this.meta);
		}

		return document;
	}

	/**
	 * Build and return a {@link SimpleModelResourceDocument} after adding a single resource using an appropriate <code>withData</code> method signature.
	 *
	 * To obtain a document with multiple resources set or without knowing whether a single or multiple resources were set use
	 * {@link #buildModelCollectionDocument()} or {@link #build()} respectively
	 *
	 * @see JsonApiModelBasedDocumentBuilder#build()
	 * @see JsonApiModelBasedDocumentBuilder#buildModelCollectionDocument()
	 * @return
	 */
	public JsonApiModelResourceDocument<T, PK> buildModelDocument() {
		if (CollectionUtils.isNotEmpty(this.resources)) {
			throw new IllegalStateException("Cannot build a JsonApiModelDocument as multiple resources are set");
		}
		return (JsonApiModelResourceDocument<T, PK>) this.build(true);
	}

	/**
	 * Build and return a {@link JsonApiModelResourceCollectionDocument} after adding resources using an appropriate <code>withData</code> method signature.
	 *
	 * To obtain a document with a single resource or without knowing whether a single or multiple resources were set use
	 * {@link #buildModelDocument()} or {@link #build()} respectively
	 *
	 * @see JsonApiModelBasedDocumentBuilder#build()
	 * @see JsonApiModelBasedDocumentBuilder#buildModelDocument()
	 * @return
	 */
	public JsonApiModelResourceCollectionDocument<T, PK> buildModelCollectionDocument() {
		if (this.resource != null) {
			throw new IllegalStateException("Cannot build a JsonApiModelCollectionDocument as a single resource is set");
		}
		return (JsonApiModelResourceCollectionDocument<T, PK>) this.build(false);
	}

	/**
	 * Called by some of <code>addData</code> signatures to set default <code>meta</code> entries
	 */
	protected void addDefaultMetaForResourcesSet() {
		int size = this.resources.size();
		this.addMeta("first", true);
		this.addMeta("last", true);
		this.addMeta("size", size);
		this.addMeta("number", 0);
		this.addMeta("numberOfElements", size);
		this.addMeta("totalElements", size);
		this.addMeta("totalPages", 1);
	}

	/**
	 * Common checks performed before adding a single resources.
	 */
	protected void ensureStateCanAcceptSingleResource() {
		if (this.resource != null) {
			throw new IllegalStateException("Was given a resource but one was already set");
		}
		if (CollectionUtils.isNotEmpty(this.errors)) {
			throw new IllegalStateException("Was given a resource but errors are already set");
		}
		if (CollectionUtils.isNotEmpty(this.resources)) {
			throw new IllegalStateException("Was given a resource but resources are already set");
		}
	}

	/**
	 * Common checks performed before adding multiple resources.
	 */
	protected void ensureStateCanAcceptMultipleResources() {
		if (this.resource != null) {
			throw new IllegalStateException("Was given a collection of models but a single resource was already set");
		}
		if (CollectionUtils.isNotEmpty(this.resources)) {
			throw new IllegalStateException("Was given a collection of models but resources are already set");
		}
		if (CollectionUtils.isNotEmpty(this.errors)) {
			throw new IllegalStateException("Cannot add resources when errors are already set");
		}
	}
}
