package myjavapackage.test;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.manosbatsis.scrudbeans.api.error.ConstraintViolationEntry;
import com.github.manosbatsis.scrudbeans.api.error.Error;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleErrorResponse implements Error {

    private String message;

    private String remoteAddress;

    private String requestMethod;

    private String requestUrl;

    private Integer httpStatusCode;

    private String httpStatusMessage;

    private String userAgent;

    private Throwable throwable;

    private Set<ConstraintViolationEntry> validationErrors;

}
