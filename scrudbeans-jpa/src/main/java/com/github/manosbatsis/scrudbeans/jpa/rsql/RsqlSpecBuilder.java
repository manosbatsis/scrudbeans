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
package com.github.manosbatsis.scrudbeans.jpa.rsql;

import java.util.ArrayList;
import java.util.List;

import com.github.manosbatsis.scrudbeans.api.domain.Model;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.jpa.specification.PredicateFactorySpecification;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

/**
 * Used by {@link RsqlSpecVisitor} to generate {@link Specification}-based predicates from individual RSQL {@link Node}s
 */
@Slf4j
public class RsqlSpecBuilder<T extends Model> {

	private final ModelInfo modelInfo;

	private final ConversionService conversionService;

	public RsqlSpecBuilder(@NonNull ModelInfo modelInfo, @NonNull ConversionService conversionService) {
		this.modelInfo = modelInfo;
		this.conversionService = conversionService;
	}

	public Specifications<T> createSpecification(Node node) {
		if (node instanceof LogicalNode) {
			return createSpecification((LogicalNode) node);
		}
		if (node instanceof ComparisonNode) {
			return createSpecification((ComparisonNode) node);
		}
		log.warn("Ignoring unknown Node type: {}", node);
		return null;
	}

	public Specifications<T> createSpecification(LogicalNode logicalNode) {
		List<Specifications<T>> specs = new ArrayList<Specifications<T>>();
		Specifications<T> temp;
		for (Node node : logicalNode.getChildren()) {
			temp = createSpecification(node);
			if (temp != null) {
				specs.add(temp);
			}
		}

		Specifications<T> result = specs.get(0);
		if (logicalNode.getOperator() == LogicalOperator.AND) {
			for (int i = 1; i < specs.size(); i++) {
				result = Specifications.where(result).and(specs.get(i));
			}
		}
		else if (logicalNode.getOperator() == LogicalOperator.OR) {
			for (int i = 1; i < specs.size(); i++) {
				result = Specifications.where(result).or(specs.get(i));
			}
		}

		return result;
	}

	public Specifications<T> createSpecification(ComparisonNode comparisonNode) {
		Specifications<T> result = Specifications.where(
				new PredicateFactorySpecification<T>(
						this.conversionService,
						this.modelInfo,
						comparisonNode.getSelector(),
						RsqlUtils.toPredicateOperator(comparisonNode.getOperator()),
						comparisonNode.getArguments()
				)
		);
		return result;
	}
}
