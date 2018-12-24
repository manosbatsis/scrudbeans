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
package com.github.manosbatsis.scrudbeans.jpa.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

	public static final String ACESS_CONTROL_CREDENTIALS_NAME = "Access-Control-Allow-Credentials";

	public static final String ACESS_CONTROL_ORIGIN_NAME = "Access-Control-Allow-Origin";

	public static final String ACESS_CONTROL_METHODS_NAME = "Access-Control-Allow-Methods";

	public static final String ACESS_CONTROL_HEADERS_NAME = "Access-Control-Allow-Headers";

	public static final String ACESS_CONTROL_MAX_AGE_NAME = "Access-Control-Max-Age";
	// MISC


	public static String getRemoteAddress(HttpServletRequest request) {
		String addresss = request.getHeader("X-FORWARDED-FOR");
		if (addresss == null) {
			addresss = request.getRemoteAddr();
		}
		return addresss;
	}

	public static String setBaseUrl(ServletRequest req) {
		HttpServletRequest request = (HttpServletRequest) req;
		String baseUrl = (String) request.getAttribute(Constants.BASE_URL_KEY);
		if (StringUtils.isBlank(baseUrl)) {
			StringBuffer url = request.getRequestURL();
			String uri = request.getRequestURI();
			String ctx = request.getContextPath();
			baseUrl = url.substring(0, url.length() - uri.length() + ctx.length());
			String scheme = request.getHeader("X-Forwarded-Proto");
			if (StringUtils.isNotBlank(scheme) && scheme.equalsIgnoreCase("HTTPS") && baseUrl.startsWith("http:")) {
				baseUrl = baseUrl.replaceFirst("http:", "https:");
			}
			request.setAttribute(Constants.BASE_URL_KEY, baseUrl);
			LOGGER.debug("Added request attribute '{}': {}", Constants.BASE_URL_KEY, baseUrl);
			request.setAttribute(Constants.DOMAIN_KEY, request.getServerName());
		}
		return baseUrl;
	}

}
