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
package com.github.manosbatsis.scrudbeans.error;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * A {@code RestErrorResolver} resolves an exception and produces a {@link ErrorResponse} instance that can be used
 * to render a Rest error representation to the response body.
 *
 * @author Les Hazlewood
 */
public interface RestErrorResolver {

	/**
	 * Returns a {@code RestError} instance to render as the response body based on the given exception.
	 *
	 * @param request current {@link ServletWebRequest} that can be used to obtain the source request/response pair.
	 * @param handler the executed handler, or <code>null</code> if none chosen at the time of the exception
	 *                (for example, if multipart resolution failed)
	 * @param ex      the exception that was thrown during handler execution
	 * @return a resolved {@code RestError} instance to render as the response body or <code>null</code> for default
	 * processing
	 */
	ErrorResponse resolveError(ServletWebRequest request, Object handler, Exception ex);
}