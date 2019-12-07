package mykotlinpackage.test

import com.github.manosbatsis.scrudbeans.api.error.ConstraintViolationEntry

data class SimpleErrorResponse(
    val title: String,
    val remoteAddress: String,
    val requestMethod: String,
    val requestUrl: String,
    val httpStatusCode: Int,
    val userAgent: String,
    val throwable: Throwable,
    val validationErrors: Set<ConstraintViolationEntry>
)