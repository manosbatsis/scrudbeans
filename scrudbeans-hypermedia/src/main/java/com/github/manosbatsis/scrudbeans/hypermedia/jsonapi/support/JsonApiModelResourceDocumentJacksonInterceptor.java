package com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jonpeterson.jackson.module.interceptor.JsonInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Adds the id of a JsonApiModelResourceDocument to the
 * child attributes object.
 */
@Slf4j
public class JsonApiModelResourceDocumentJacksonInterceptor implements JsonInterceptor {

    @Override
    public JsonNode intercept(JsonNode node, JsonNodeFactory nodeFactory) {
        log.warn("INTERCEPT BEFORE: "+node);
        if(!Objects.isNull(node) && node.isObject()) {
            JsonNode idNode = node.get("id");
            JsonNode attributesNode = node.get("attributes");
            if(!Objects.isNull(idNode)
                    && !Objects.isNull(attributesNode)
                    && attributesNode.isObject()){
                ((ObjectNode) attributesNode).set("id", idNode);
            }
        }
        log.warn("INTERCEPT AFTER: "+node);
        return node;
    }
}