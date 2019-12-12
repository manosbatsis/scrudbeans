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
package com.github.manosbatsis.scrudbeans.error;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.manosbatsis.scrudbeans.api.error.ConstraintViolationEntry;
import com.github.manosbatsis.scrudbeans.api.error.Error;
import com.github.manosbatsis.scrudbeans.api.exception.ConstraintViolationException;
import com.github.manosbatsis.scrudbeans.api.util.HttpUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.http.HttpStatus;


@Schema(name = "Error", description = "Created exclusively by the system " +
        "(i.e. without manual intervention) to handle and inform the user about runtime exceptions. ")
@JsonPropertyOrder({"title", "createdDate", "httpStatusCode", "requestMethod", "requestUrl", "validationErrors", "user"})
@Data
public class ErrorResponse implements Error {
    @Schema(description = "The error title", readOnly = true)
    private String title;

    @Schema(description = "The address the request originated from", readOnly = true)
    private String remoteAddress;

    @Schema(description = "The HTTP request method")
    private String requestMethod;

    @Schema(description = "The HTTP request URL, relative to system base URL")
    private String requestUrl;

    @Schema(description = "The HTTP response status code ")
    private Integer httpStatusCode;

    @Schema(description = "The UA string if provided with a request")
    private String userAgent;

    @Schema(description = "Failed constraint validation rules, if any")
    private Set<ConstraintViolationEntry> validationErrors;

    @JsonIgnore
    private Map<String, String> responseHeaders;

    @JsonIgnore
    private Throwable throwable;

    protected ErrorResponse(HttpServletRequest request, String title) {
        this.setTitle(title);
		// note reguest details
		this.addRequestInfo(request);
	}

	public ErrorResponse(HttpServletRequest request, String message, Integer httpStatusCode, Throwable throwable) {
		this(request, message);
		this.throwable = throwable;
		if (httpStatusCode == null) {
			throw new NullPointerException("httpStatusCode argument cannot be null.");
		}
		// set status
		this.httpStatusCode = httpStatusCode;

		// set default title if appropriate
		if (StringUtils.isBlank(message)) {
			this.setTitle(HttpStatus.valueOf(httpStatusCode).getReasonPhrase());
		}

		// add validation errors, if any
		if (ConstraintViolationException.class.isAssignableFrom(throwable.getClass())) {
			Set<ConstraintViolation> violations = ((ConstraintViolationException) throwable)
					.getConstraintViolations();
			if (CollectionUtils.isNotEmpty(violations)) {
				this.validationErrors = new HashSet<>();
				for (ConstraintViolation violation : violations) {
					this.validationErrors.add(new ConstraintViolationEntry(violation));
				}
			}
		}
	}

	public void addRequestInfo(HttpServletRequest request) {
		if (request != null) {

			this.remoteAddress = HttpUtil.getRemoteAddress(request);
			this.userAgent = request.getHeader("User-Agent");

			this.requestMethod = request.getMethod();
			String baseUrl = HttpUtil.setBaseUrl(request);
			StringBuffer reUrl = request.getRequestURL();

			// add query string if any
			String queryString = request.getQueryString();
			if (StringUtils.isNoneBlank(queryString)) {
				reUrl.append('?').append(queryString);
			}

			this.requestUrl = reUrl.substring(baseUrl.length());
		}
	}
}