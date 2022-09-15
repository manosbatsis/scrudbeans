package mykotlinpackage.test

// import com.github.manosbatsis.scrudbeans.api.error.ConstraintViolationEntry

data class SimpleErrorResponse(
    var message: String? = null,
    var remoteAddress: String? = null,
    var requestMethod: String? = null,
    var requestUrl: String? = null,
    var httpStatusCode: Int? = null,
    var httpStatusMessage: String? = null,
    var userAgent: String? = null,
    var throwable: Throwable? = null,
    // var validationErrors: Set<ConstraintViolationEntry>? = null
)
