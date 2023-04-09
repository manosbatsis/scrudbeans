package com.github.manosbatsis.scrudbeans.logging

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils
import java.io.IOException
import java.nio.charset.Charset

class RequestResponseLoggingInterceptor : ClientHttpRequestInterceptor {

    companion object {
        private val logger = loggerFor<RequestResponseLoggingInterceptor>()
        private val utf8: Charset = Charset.forName("UTF-8")
    }

    @Throws(IOException::class)
    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        logRequest(request, body)
        val response = execution.execute(request, body)
        logResponse(response)
        return response
    }

    @Throws(IOException::class)
    private fun logRequest(request: HttpRequest, body: ByteArray) {
        logger.info("Request head: {} {} {}", request.method, request.uri, request.headers)
        logger.info("Request body: {}", String(body, utf8))
    }

    @Throws(IOException::class)
    private fun logResponse(response: ClientHttpResponse) {
        logger.info("Response head: {} {} {}", response.statusCode, response.statusText, response.headers)
        logger.info("Response body: {}", StreamUtils.copyToString(response.body, utf8))
    }
}
