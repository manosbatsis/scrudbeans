package com.github.manosbatsis.scrudbeans.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

/**
 * Replaces {@link org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController}
 */
@Controller("basicErrorController")
@Primary
@RequestMapping(BasicErrorController.PATH)
public class BasicErrorController implements ErrorController, PriorityOrdered {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicErrorController.class);
    public static final String PATH = "/api/rest/error";

    @Autowired
    private ErrorAttributes errorAttributes;


    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> error(ServletWebRequest request, Exception ex) {
        Map<String, Object> body = errorAttributes.getErrorAttributes(request, true);
        return new ResponseEntity(body, HttpStatus.resolve(request.getResponse().getStatus()));
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Map<String, Object>> errorHtml(ServletWebRequest request, Exception ex) {
        Map<String, Object> body = errorAttributes.getErrorAttributes(request, true);
        return new ResponseEntity(body, HttpStatus.resolve(request.getResponse().getStatus()));
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
