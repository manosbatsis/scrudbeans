package com.github.manosbatsis.scrudbeans.test

import org.springframework.core.ParameterizedTypeReference

inline fun <reified T> parameterizedTypeReference() = object : ParameterizedTypeReference<T>() {}