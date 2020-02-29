/**
 * ScrudBeans: Model driven development for Spring Boot
 * -------------------------------------------------------------------
 * <p>
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Base interface for model objects
 *
 * @param <PK> The primary key type, if any
 * @deprecated Not required or used anymore. Use either org.springframework.data.domain.Persistable for Java or com.github.manosbatsis.scrudbeans.api.domain.KPersistable for Kotlin
 */
@Deprecated
public interface Persistable<PK extends Serializable> extends Serializable {


    /**
     * Get the entity's primary key.
     */
    @JsonIgnore
    PK getScrudBeanId();

    /**
     * Whether the instance is transient
     */
    @JsonIgnore
    boolean isNew();
}
