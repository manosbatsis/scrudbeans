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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.manosbatsis.scrudbeans.api.domain.PersistableModel;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.api.specification.PredicateOperator;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import lombok.NonNull;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by manos on 2/3/2017.
 */
public class RsqlUtils {

	public static final Set<ComparisonOperator> OPERATORS = RSQLOperators.defaultOperators();

	private static final Map<ComparisonOperator, PredicateOperator> operatorMappings = new HashMap<>();

	private static final String AUTO = "=auto=";

	static {
		operatorMappings.put(RSQLOperators.EQUAL, PredicateOperator.EQUAL);
		operatorMappings.put(RSQLOperators.GREATER_THAN, PredicateOperator.GREATER_THAN);
		operatorMappings.put(RSQLOperators.GREATER_THAN_OR_EQUAL, PredicateOperator.GREATER_THAN_OR_EQUAL);
		operatorMappings.put(RSQLOperators.IN, PredicateOperator.IN);
		operatorMappings.put(RSQLOperators.LESS_THAN, PredicateOperator.LESS_THAN);
		operatorMappings.put(RSQLOperators.LESS_THAN_OR_EQUAL, PredicateOperator.LESS_THAN_OR_EQUAL);
		operatorMappings.put(RSQLOperators.NOT_EQUAL, PredicateOperator.NOT_EQUAL);
		operatorMappings.put(RSQLOperators.NOT_IN, PredicateOperator.NOT_IN);

		ComparisonOperator auto = new ComparisonOperator(AUTO, true);
		OPERATORS.add(auto);
		operatorMappings.put(auto, PredicateOperator.AUTO);
	}

	/**
	 * Parse  the given (request URL) parameters map into RSQL (NOTE that if RSQL is present under the "filter" key, all other <code>paramsMap</code> entries will be ignored
	 * @param modelInfo the root model info
	 * @param conversionService the conversion service to use for values
	 * @param paramsMap the (request URL) parameters map
	 * @param implicitCriteria
	 * @param <M>
	 * @param <MID>
	 * @param ignoreNamesForSpecification the URL parameter names to ignore if no <code>filter</code>> param is present
	 * @return
	 */
	public static <M extends PersistableModel<MID>, MID extends Serializable> Specification<M> buildtSpecification(
			ModelInfo<M, MID> modelInfo,
			ConversionService conversionService,
			Map<String, String[]> paramsMap,
			Map<String, String[]> implicitCriteria,
			String[] ignoreNamesForSpecification) {

		Specification<M> spec = null;

		// check for RSQL in JSON API "filter" parameter,
		// convert simple URL params to RSQL if missing
		String rsql = ArrayUtils.isNotEmpty(paramsMap.get("filter")) ? paramsMap.get("filter")[0] : RsqlUtils.toRsql(paramsMap, ignoreNamesForSpecification);

		// if any, append implicit params as mandatory
		if (MapUtils.isNotEmpty(implicitCriteria)) {
			StringBuffer b = new StringBuffer();

			// if we have a "base" RSQL string
			if (StringUtils.isNotBlank(rsql)) {
				// (baseRsql) and ...
				b.append("(").append(rsql).append(");");
			}

			// add implicit RSQL
			b.append("(").append(RsqlUtils.toRsql(implicitCriteria)).append(")");

			// replace
			rsql = b.toString();
		}

		// if resulting string is not empty,
		// build specification
		if (StringUtils.isNotBlank(rsql)) {
			Node rootNode = RsqlUtils.parse(rsql);
			spec = rootNode.accept(new RsqlSpecVisitor<M>(modelInfo, conversionService));
		}
		return spec;
	}

	public static PredicateOperator toPredicateOperator(@NonNull ComparisonOperator comparisonOperator) {
		return operatorMappings.get(comparisonOperator);
	}

	public static Node parse(String rsql) {
		Node node = null;
		if (StringUtils.isNotBlank(rsql)) {
			node = new RSQLParser(RsqlUtils.OPERATORS).parse(rsql);
		}
		return node;
	}

	/**
	 *
	 * @param urlParams
	 * @param ignoredNames the URL parameter names to ignore if no <code>filter</code>> param is present
	 * @return
	 */
	public static String toRsql(Map<String, String[]> urlParams, String... ignoredNames) {
		Set<String> uniqueNames;
		if (ignoredNames != null) {
			uniqueNames = new HashSet<String>(Arrays.asList(ignoredNames));
		}
		else {
			uniqueNames = Collections.emptySet();
		}
		return toRsql(urlParams, uniqueNames);
	}

	public static String toRsql(@NonNull Map<String, String[]> urlParams, @NonNull Set<String> ignoredNames) {
		StringBuffer rsql = new StringBuffer();
		// iterate parameters
		for (String paramName : urlParams.keySet()) {
			// if not reserved name
			if (!ignoredNames.contains(paramName)) {
				// get val;ues
				String[] values = urlParams.get(paramName);
				// ensure non-null values
				if (ArrayUtils.isNotEmpty(values)) {
					rsql.append(";").append(paramName);
					// use equals/in operator for single/multiple values respectively
					if (values.length == 1) {
						rsql.append(AUTO).append(values[0]);
					}
					else {
						rsql.append(AUTO).append("('").append(values[0]).append("'");
						for (int i = 1; i < values.length; i++) {
							rsql.append(",'").append(values[i]).append("'");
						}
						rsql.append(")");
					}
				}

			}
		}
		String rsqlString = rsql.toString();
		// remove leading comma
		if (StringUtils.isNotEmpty(rsqlString)) {
			rsqlString = rsqlString.substring(1);
		}
		return rsqlString;
	}
}
