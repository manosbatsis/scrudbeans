package myjavapackage.test;

import java.util.Set;

import com.github.manosbatsis.scrudbeans.api.error.ConstraintViolationEntry;
import com.github.manosbatsis.scrudbeans.api.error.Error;
import lombok.Data;

@Data
public class SimpleErrorResponse implements Error {

	private String title;

	private String remoteAddress;

	private String requestMethod;

	private String requestUrl;

	private Integer httpStatusCode;

	private String userAgent;

	private Throwable throwable;

	private Set<ConstraintViolationEntry> validationErrors;

}
