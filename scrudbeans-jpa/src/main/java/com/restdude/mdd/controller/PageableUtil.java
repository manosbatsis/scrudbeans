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
package com.restdude.mdd.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.Assert;

public class PageableUtil {

	public static Pageable buildPageable(Integer page, Integer size, String sort) {
		Assert.isTrue(page >= 0, "Page index must be greater than, or equal to, 0");


		Sort sortObject = PageableUtil.buildSort(sort);
		Pageable pageable = new PageRequest(page, size, sortObject);

		return pageable;
	}

	/**
	 * Handles a <code>sort</code> parameter value as defined in JSON API
	 * @param sort
	 * @return
	 *
	 * @see <a href="http://jsonapi.org/format/upcoming/#fetching-sorting">JSON API 1.x, Paging and Sorting</a>
	 */
	public static Sort buildSort(String sort) {
		Sort pageableSort = null;
		if (StringUtils.isNotBlank(sort)) {
			String[] sortProps = sort.split(",");
			if (ArrayUtils.isNotEmpty(sortProps)) {
				List<Order> orders = new ArrayList<Order>(sortProps.length);
				for (String prop : sortProps) {
					if (prop.startsWith("-")) {
						orders.add(new Order(Sort.Direction.DESC, prop.substring(1)));
					}
					else {
						orders.add(new Order(Sort.Direction.ASC, prop));
					}

				}
				pageableSort = new Sort(orders);
			}
		}
		return pageableSort;
	}

	public static Sort buildSort(String sort, String direction) {
		Sort pageableSort = null;
		if (sort != null && direction != null) {
			List<Order> orders = null;
			Order order = new Order(
					direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC
							: Sort.Direction.DESC, sort);
			orders = new ArrayList<Order>(1);
			orders.add(order);
			pageableSort = new Sort(orders);
		}
		return pageableSort;
	}
}
