package mykotlinpackage.test

import com.github.manosbatsis.scrudbeans.service.IdentifierAdapterRegistry
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiErrorResponse
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiFieldError
import mykotlinpackage.ScrudBeansSampleApplication
import mykotlinpackage.model.DiscountCode
import mykotlinpackage.service.OrderService
import mykotlinpackage.service.ProductService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [ScrudBeansSampleApplication::class], webEnvironment = RANDOM_PORT)
class RestErrorsIT {

    @Autowired lateinit var restTemplateOrig: TestRestTemplate

    @Autowired lateinit var productService: ProductService

    @Autowired lateinit var orderService: OrderService

    @Autowired lateinit var identifierAdapterRegistry: IdentifierAdapterRegistry

    val restTemplate: TestRestTemplate by lazy {
        restTemplateOrig.restTemplate.setRequestFactory(BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory()))
        // restTemplateOrig.restTemplate.interceptors.add(RequestResponseLoggingInterceptor())
        restTemplateOrig
    }

    @Test
    fun testNotFound() {
        val error: ApiErrorResponse = restTemplate.exchange(
            "/api/rest/products/${UUID.randomUUID()}",
            HttpMethod.GET,
            null,
            ApiErrorResponse::class.java,
        ).let {
            Assertions.assertThat(it.statusCode.value()).isEqualTo(HttpStatus.NOT_FOUND.value())
            Assertions.assertThat(it.body).isNotNull
            it.body!!
        }
    }

    @Test
    fun testBadRequestNotNullConstraint() {
        val discountCode = DiscountCode(code = "DISCOUNT_testBadRequestNotNullConstraint")
        val error: ApiErrorResponse = restTemplate.exchange(
            "/api/rest/discountCodes",
            HttpMethod.POST,
            HttpEntity(discountCode),
            ApiErrorResponse::class.java,
        ).let {
            Assertions.assertThat(it.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
            Assertions.assertThat(it.body).isNotNull
            it.body!!
        }
        assertFalse(error.fieldErrors.isEmpty())
        val violations: List<ApiFieldError> = error.fieldErrors
        assertEquals(1, violations.size)
        val violation = violations.iterator().next()
        assertEquals("must not be null", violation.message)
        assertEquals("percentage", violation.path)
    }
}
