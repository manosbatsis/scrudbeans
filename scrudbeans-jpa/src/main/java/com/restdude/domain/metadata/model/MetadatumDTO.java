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
package com.restdude.domain.metadata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restdude.domain.MetadataSubjectModel;
import com.restdude.domain.MetadatumModel;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class MetadatumDTO implements MetadatumModel {

	private static final long serialVersionUID = -1468517690700208260L;

	@JsonIgnore
	private MetadataSubjectModel subject;

	private String predicate;

	private String object;

	public MetadatumDTO() {

	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!MetadatumDTO.class.isInstance(obj)) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		MetadatumDTO that = (MetadatumDTO) obj;
		return new EqualsBuilder().append(this.getSubject(), that.getSubject())
				.append(this.getPredicate(), that.getPredicate()).isEquals();
	}

	@Override
	public MetadataSubjectModel getSubject() {
		return subject;
	}

	@Override
	public void setSubject(MetadataSubjectModel subject) {
		this.subject = subject;
	}

	@Override
	public String getPredicate() {
		return predicate;
	}

	@Override
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	@Override
	public String getObject() {
		return object;
	}

	@Override
	public void setObject(String object) {
		this.object = object;
	}

}
