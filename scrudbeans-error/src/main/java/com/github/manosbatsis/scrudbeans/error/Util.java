package com.github.manosbatsis.scrudbeans.error;

import com.github.manosbatsis.scrudbeans.api.exception.NotFoundException;
import com.github.manosbatsis.scrudbeans.api.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.util.NestedServletException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION;

public class Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";
    private static final String SERVLET_ERROR_ATTRIBUTE = "javax.servlet.error.exception";

    private static Map<String, Integer> exceptionStatuses = new HashMap<>();

    static {
        exceptionStatuses.put(AuthenticationException.class.getCanonicalName(), HttpServletResponse.SC_UNAUTHORIZED);
        exceptionStatuses.put(UsernameNotFoundException.class.getCanonicalName(), HttpServletResponse.SC_UNAUTHORIZED);
        exceptionStatuses.put(AccessDeniedException.class.getCanonicalName(), HttpServletResponse.SC_UNAUTHORIZED);
        exceptionStatuses.put("org.hibernate.ObjectNotFoundException", HttpServletResponse.SC_NOT_FOUND);
        exceptionStatuses.put(NotFoundException.class.getCanonicalName(), HttpServletResponse.SC_NOT_FOUND);
        exceptionStatuses.put(FileNotFoundException.class.getCanonicalName(), HttpServletResponse.SC_NOT_FOUND);
        exceptionStatuses.put(EntityNotFoundException.class.getCanonicalName(), HttpServletResponse.SC_NOT_FOUND);
        exceptionStatuses.put(EntityExistsException.class.getCanonicalName(), HttpServletResponse.SC_CONFLICT);
        exceptionStatuses.put(HttpRequestMethodNotSupportedException.class.getCanonicalName(), HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        exceptionStatuses.put(HttpMediaTypeNotSupportedException.class.getCanonicalName(), HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        exceptionStatuses.put(HttpMediaTypeNotAcceptableException.class.getCanonicalName(), HttpServletResponse.SC_NOT_ACCEPTABLE);
        exceptionStatuses.put(MissingPathVariableException.class.getCanonicalName(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        exceptionStatuses.put("org.springframework.dao.DataIntegrityViolationException", HttpServletResponse.SC_BAD_REQUEST);
        exceptionStatuses.put(MissingServletRequestParameterException.class.getCanonicalName(), HttpServletResponse.SC_BAD_REQUEST);
        exceptionStatuses.put(ServletRequestBindingException.class.getCanonicalName(), HttpServletResponse.SC_BAD_REQUEST);
        exceptionStatuses.put(ValidationException.class.getCanonicalName(), HttpServletResponse.SC_BAD_REQUEST);
        exceptionStatuses.put(ConversionNotSupportedException.class.getCanonicalName(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        exceptionStatuses.put(TypeMismatchException.class.getCanonicalName(), HttpServletResponse.SC_BAD_REQUEST);
        exceptionStatuses.put(HttpMessageNotReadableException.class.getCanonicalName(), HttpServletResponse.SC_BAD_REQUEST);
        exceptionStatuses.put(HttpMessageNotWritableException.class.getCanonicalName(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        exceptionStatuses.put(MethodArgumentNotValidException.class.getCanonicalName(), HttpServletResponse.SC_BAD_REQUEST);
        exceptionStatuses.put(MissingServletRequestPartException.class.getCanonicalName(), HttpServletResponse.SC_BAD_REQUEST);
        exceptionStatuses.put(BindException.class.getCanonicalName(), HttpServletResponse.SC_BAD_REQUEST);
        exceptionStatuses.put(NoHandlerFoundException.class.getCanonicalName(), HttpServletResponse.SC_NOT_FOUND);
        exceptionStatuses.put("com.github.manosbatsis.vaultaire.service.node.NotFoundException", HttpServletResponse.SC_NOT_FOUND);
        exceptionStatuses.put(AsyncRequestTimeoutException.class.getCanonicalName(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        exceptionStatuses.put(RuntimeException.class.getCanonicalName(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        exceptionStatuses.put(Exception.class.getCanonicalName(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public static <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    public static void storeErrorAttribute(ServletWebRequest request, Throwable ex) {
        LOGGER.debug("storeErrorAttribute(ServletWebRequest): {}", ex);
        request.setAttribute(ERROR_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
    }

    public static void storeErrorAttribute(HttpServletRequest request, Throwable ex) {
        LOGGER.debug("storeErrorAttribute (HttpServletRequest): {}", ex);
        request.setAttribute(ERROR_ATTRIBUTE, ex);
    }

    public static Throwable getErrorAttribute(WebRequest webRequest) {
        Throwable exception = getAttribute(webRequest, ERROR_ATTRIBUTE);
        if (exception == null) {
            exception = getAttribute(webRequest, SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE);
        }
        if (exception == null) {
            exception = getAttribute(webRequest, SERVLET_ERROR_ATTRIBUTE);
        }
        LOGGER.debug("getErrorAttribute: {}", exception);
        return exception;
    }

    public static HttpStatus getHttpStatus(Throwable ex) {
        HttpStatus status = null;
        if (ex != null) {
            // Unwrap if nested
            if(NestedServletException.class.isAssignableFrom(ex.getClass())){
                ex = ex.getCause();
            }
            // if ResponseStatusException
            if (ResponseStatusException.class.isAssignableFrom(ex.getClass())) {
                status = ((ResponseStatusException) ex).getStatus();
            }
            // if SystemException
            else if (SystemException.class.isAssignableFrom(ex.getClass())) {
                status = ((SystemException) ex).getStatus();
            } else if (ex.getMessage().toLowerCase().contains("detached entity passed to persist")
                    || ex.getMessage().toLowerCase().contains("A different object with the same identifier value")) {
                status = HttpStatus.CONFLICT;
            } else {
                status = getStandardExceptionHttpStatus(ex);
            }
        }
        return status;
    }


    protected static HttpStatus getStandardExceptionHttpStatus(Throwable ex) {
        Class exceptionClass = ex.getClass();
        Integer statusCode = null;
        while (statusCode == null) {
            statusCode = exceptionStatuses.get(exceptionClass.getCanonicalName());

            LOGGER.debug("getStandardExceptionHttpStatus: {} for {}", statusCode, exceptionClass.getCanonicalName());
            if (statusCode == null) {
                exceptionClass = exceptionClass.getSuperclass();
            }
        }
        return HttpStatus.valueOf(statusCode.intValue());
    }

    public static void printRequestProps(WebRequest webRequest) {

        LOGGER.debug("printRequestProps SCOPE_SESSION");
        for (String name : webRequest.getAttributeNames(SCOPE_SESSION)) {
            if (name.toLowerCase().contains("error") || name.toLowerCase().contains("exception"))
                LOGGER.debug("printRequestProps attr, name: {}, has value: {}", name, webRequest.getAttribute(name, SCOPE_SESSION) != null);
        }
        LOGGER.debug("printRequestProps SCOPE_REQUEST");
        for (String name : webRequest.getAttributeNames(SCOPE_REQUEST)) {
            if (name.toLowerCase().contains("error") || name.toLowerCase().contains("exception"))
                LOGGER.debug("printRequestProps attr, name: {}, has value: {}", name, webRequest.getAttribute(name, SCOPE_REQUEST) != null);
        }
    }
}
