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
package com.github.manosbatsis.scrudbeans.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.manosbatsis.scrudbeans.api.error.ConstraintViolationEntry;
import com.github.manosbatsis.scrudbeans.api.error.Error;
import com.github.manosbatsis.scrudbeans.api.exception.ConstraintViolationException;
import com.github.manosbatsis.scrudbeans.api.util.HttpUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.ServletWebRequest;


@Schema(name = "Error", description = "Created exclusively by the system " +
        "(i.e. without manual intervention) to handle and inform the user about runtime exceptions. ")
@JsonPropertyOrder({"message", "description", "createdDate", "httpStatusCode", "requestMethod", "requestUrl", "validationErrors", "user"})
@Data
public class ScrudbeansErrorResponse implements Error {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrudbeansErrorResponse.class);
    @Schema(description = "A short description of the error", readOnly = true)
    private String message;

    @Schema(description = "The address the request originated from", readOnly = true)
    private String remoteAddress;

    @Schema(description = "The HTTP request method")
    private String requestMethod;

    @Schema(description = "The HTTP request URL, relative to system base URL")
    private String requestUrl;

    @Schema(description = "The HTTP response status code ")
    private Integer httpStatusCode;

    @Schema(description = "The HTTP error message", readOnly = true)
    private String httpStatusMessage;

    @Schema(description = "The user ID for the request")
    private String user;

    @Schema(description = "The UA string if provided with a request")
    private String userAgent;

    @Schema(description = "The exception class")
    private String exceptionClass;

    @Schema(description = "The exception stacktrace")
    private String exceptionStacktrace;

    @Schema(description = "Failed constraint validation rules, if any")
    private Set<ConstraintViolationEntry> validationErrors;

    @JsonIgnore
    private Map<String, String> responseHeaders;


    public ScrudbeansErrorResponse(
            @NonNull ServletWebRequest request, String message, HttpStatus status, Throwable throwable, @NonNull boolean includeStackTrace) {
        LOGGER.warn("ScrudbeansErrorResponse, throwable:", throwable);
        if (StringUtils.isBlank(message) && !Objects.isNull(throwable)) message = throwable.getMessage();
        if (!Objects.isNull(throwable)) {
            this.exceptionClass = throwable.getClass().getCanonicalName();
            if (includeStackTrace) {
                StringWriter stackTrace = new StringWriter();
                throwable.printStackTrace(new PrintWriter(stackTrace));
                stackTrace.flush();
                this.exceptionStacktrace = stackTrace.toString();
            }
            if (Objects.isNull(status)) {
                status = Util.getHttpStatus(throwable);
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
        if (Objects.isNull(status)) {
            HttpServletResponse response = request.getResponse();
            if (!Objects.isNull(response)) {
                int statusCode = response.getStatus();
                status = HttpStatus.resolve(statusCode);
            }
        }
        if (!Objects.isNull(status)) {
            this.setHttpStatusMessage(status.getReasonPhrase());
            this.setHttpStatusCode(status.value());
        }
        this.setMessage(message);
        this.addRequestInfo(request.getRequest());

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

            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                this.user = UserDetails.class.isAssignableFrom(principal.getClass())
                        ? ((UserDetails) principal).getUsername()
                        : principal.getName();
            }

            this.requestUrl = reUrl.substring(baseUrl.length());
        }
    }
}