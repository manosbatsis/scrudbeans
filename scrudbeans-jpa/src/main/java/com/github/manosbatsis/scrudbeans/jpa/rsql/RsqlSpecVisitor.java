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
package com.github.manosbatsis.scrudbeans.jpa.rsql;

import com.github.manosbatsis.scrudbeans.api.domain.Model;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.NonNull;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;

/**
 * Used to generate a {@link Specification}-based query from a root RSQL {@link cz.jirutka.rsql.parser.ast.Node}
 */
public class RsqlSpecVisitor<T extends Model> implements RSQLVisitor<Specification<T>, Void> {

	private RsqlSpecBuilder<T> builder;

	public RsqlSpecVisitor(@NonNull ModelInfo modelInfo, @NonNull ConversionService conversionService) {
		builder = new RsqlSpecBuilder<T>(modelInfo, conversionService);
	}

	@Override
	public Specification<T> visit(AndNode node, Void param) {
		return builder.createSpecification(node);
	}

	@Override
	public Specification<T> visit(OrNode node, Void param) {
		return builder.createSpecification(node);
	}

	@Override
	public Specification<T> visit(ComparisonNode node, Void params) {
		return builder.createSpecification(node);
	}
}