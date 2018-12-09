package com.restdude.mdd.annotation.model;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a field as a computed relationship
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ComputedRelationship {
}