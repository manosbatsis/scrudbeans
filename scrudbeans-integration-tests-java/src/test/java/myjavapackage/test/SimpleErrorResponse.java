package myjavapackage.test;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleErrorResponse {

    private String message;

    private String remoteAddress;

    private String requestMethod;

    private String requestUrl;

    private Integer httpStatusCode;

    private String httpStatusMessage;

    private String userAgent;

    private Throwable throwable;


}
