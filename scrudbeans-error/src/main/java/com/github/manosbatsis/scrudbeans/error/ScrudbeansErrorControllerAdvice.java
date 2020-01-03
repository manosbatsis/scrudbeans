package com.github.manosbatsis.scrudbeans.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * Maps exceptions to HTTP responses
 */
@ConditionalOnProperty(name = "scrudbeans.errors.controlleradvice", havingValue = "true", matchIfMissing = true)
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ScrudbeansErrorControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrudbeansErrorControllerAdvice.class);
    private String currentApiVersion;
    private String sendReportUri;
    private boolean includeException;
    private ScrudbeansErrorAttributes errorAttributes;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ScrudbeansErrorResponse> handleError(ServletWebRequest request, Exception ex) {
        Util.storeErrorAttribute(request, ex);
        LOGGER.warn("ScrudbeansErrorControllerAdvice handleError, exception:", ex);
        ScrudbeansErrorResponse scrudbeansErrorResponse =
                errorAttributes.getErrorResponse(request, ex, includeException);
        return new ResponseEntity(scrudbeansErrorResponse, HttpStatus.resolve(scrudbeansErrorResponse.getHttpStatusCode()));
    }

    @Value("${scrudbeans.api.version:1.0}")
    public void setCurrentApiVersion(String currentApiVersion) {
        this.currentApiVersion = currentApiVersion;
    }

    @Value("${scrudbeans.sendreport.uri:none}")
    public void setSendReportUri(String sendReportUri) {
        this.sendReportUri = sendReportUri;
    }

    @Value("${server.error.include-exception:false}")
    public void setIncludeException(boolean includeException) {
        this.includeException = includeException;
    }

    @Autowired
    @Qualifier("errorAttributes")
    public void setErrorAttributes(ScrudbeansErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }
}
