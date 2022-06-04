package com.github.manosbatsis.scrudbeans.extensions

import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field
import java.util.Objects.isNull
import jakarta.persistence.Embedded
import jakarta.persistence.Entity

val Field.isEmbedded: Boolean
    get() = this.isAnnotationPresent(Embedded::class.java)

val Field.isFromEntity: Boolean
    get() = this.type.isAnnotationPresent(Entity::class.java)

fun <T> Field.value(entity: T): Any? {
    this.isAccessible = true
    return ReflectionUtils.getField(this, entity)
}

fun <T> Field.isFieldValueNull(entity: T): Boolean {
    return isNull(this.value(entity))
}