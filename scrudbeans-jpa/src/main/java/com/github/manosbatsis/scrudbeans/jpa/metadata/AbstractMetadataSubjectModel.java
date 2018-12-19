/**
 *
 * Restdude
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
package com.github.manosbatsis.scrudbeans.jpa.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.manosbatsis.scrudbeans.api.domain.MetadataSubjectModel;
import com.github.manosbatsis.scrudbeans.api.domain.MetadatumModel;
import com.github.manosbatsis.scrudbeans.jpa.domain.AbstractBasicAuditedModel;
import net.jodah.typetools.TypeResolver;

import org.springframework.util.CollectionUtils;

/**
 * Abstract base persistent class for metadata bearing classes. Implementations
 * can override relational specifics via javax.persistence.AssociationOverride
 * annotations
 */
@MappedSuperclass
public abstract class AbstractMetadataSubjectModel<M extends MetadatumModel>
		extends AbstractBasicAuditedModel implements MetadataSubjectModel<M> {

	private static final long serialVersionUID = -1468517690700208260L;

	@OneToMany(mappedBy = "subject", fetch = FetchType.EAGER)
	@MapKey(name = "predicate")
	@JsonDeserialize(using = MetadataMapDeserializer.class)
	@JsonSerialize(contentUsing = MetadatumToStringValueSerializer.class)
	private Map<String, M> metadata;

	public AbstractMetadataSubjectModel() {
		super();
	}

	@JsonIgnore
	@Override
	public abstract Class<M> getMetadataDomainClass();

	@Override
	public Map<String, M> getMetadata() {
		return metadata;
	}

	@Override
	public void setMetadata(Map<String, M> metadata) {
		this.metadata = metadata;
	}

	@Override
	public M addMetadatum(M metadatum) {
		if (this.getMetadata() == null) {
			this.setMetadata(new HashMap<String, M>());
		}
		metadatum.setSubject(this);
		return this.getMetadata().put(metadatum.getPredicate(), metadatum);
	}

	@Override
	public void addMetadata(Collection<M> metadata) {
		if (!CollectionUtils.isEmpty(metadata)) {
			for (M metadatum : metadata) {
				this.addMetadatum(metadatum);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public M addMetadatum(String predicate, String object) {
		Class<?> metadatumClass = TypeResolver.resolveRawArgument(
				AbstractMetadataSubjectModel.class, getClass());
		M metadatum = null;
		try {
			metadatum = (M) metadatumClass.getConstructor(String.class,
					String.class).newInstance(predicate, object);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed adding metadatum", e);
		}
		return this.addMetadatum(metadatum);
	}

}