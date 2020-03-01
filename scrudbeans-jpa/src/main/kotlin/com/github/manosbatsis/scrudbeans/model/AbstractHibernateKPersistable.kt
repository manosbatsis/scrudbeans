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
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.model
import com.github.manosbatsis.scrudbeans.api.domain.KPersistable
import org.hibernate.annotations.Formula

/**
 * Convenient `@Formula`-based implementation of [KPersistable] for Kotlin
 */
abstract class AbstractHibernateKPersistable<PK> : KPersistable<PK>{

    @Formula(" true ")
    private var persisted: Boolean = false
    override fun isNew(): Boolean = !persisted
}