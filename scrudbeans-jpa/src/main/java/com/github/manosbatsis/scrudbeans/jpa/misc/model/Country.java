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
package com.github.manosbatsis.scrudbeans.jpa.misc.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.jpa.model.AbstractAssignedIdPersistableModel;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.hateoas.core.Relation;

/**
 * Class to represent a country, including ISO 3166-1 alpha-2 code, name,
 * languages, capital and currency, native name, calling codes.
 */

@DiffIgnore
@Entity
@Table(name = "country")
@AttributeOverrides({
		@AttributeOverride(name = "id", column = @Column(unique = true, nullable = false, length = 2)),
		@AttributeOverride(name = "name", column = @Column(unique = true, nullable = false, length = 50)),
})
@ScrudBean(pathFragment = "countries", apiName = "Countries", apiDescription = "Operations about countries"
		/*TODO , controllerSuperClass = CountryController.class*/)
@ApiModel(value = "Country", description = "A model representing a country, meaning a region that is identified as a distinct entity in political geography.")
@Relation(value = "country", collectionRelation = "countries")
public class Country extends AbstractAssignedIdPersistableModel<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Country.class);

	@NotNull @Getter @Setter
	@Column(name = "name", nullable = false)
	private String name;

	@Getter @Setter
	@Column(name = "native_name", unique = true, nullable = true, length = 50)
	private String nativeName;

	@Getter @Setter
	@Column(name = "calling_code", unique = false, nullable = true, length = 15)
	private String callingCode;

	@Getter @Setter
	@Column(unique = false, nullable = true, length = 50)
	private String capital;

	@Getter @Setter
	@Column(unique = false, nullable = true, length = 30)
	private String currency;

	@Getter @Setter
	@Column(unique = false, nullable = true, length = 30)
	private String languages;

	public Country() {
		super();
	}

	public Country(String id) {
		this.setId(id);
	}

	public Country(String id, String name, String nativeName, String callingCode, String capital,
			String currency, String languages) {
		super(id);
		this.name = name;
		this.nativeName = nativeName;
		this.callingCode = callingCode;
		this.capital = capital;
		this.currency = currency;
		this.languages = languages;
	}

	@Override
	public boolean equals(final Object obj) {
		if (Country.class.isAssignableFrom(obj.getClass())) {
			final Country other = (Country) obj;
			return new EqualsBuilder()
					.appendSuper(super.equals(other))
					.append(this.getName(), other.getName())
					.isEquals();
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
				.append(this.getName())
				.toHashCode();
	}
}