package mykotlinpackage.test

import com.github.manosbatsis.scrudbeans.api.error.ConstraintViolationEntry
import com.github.manosbatsis.scrudbeans.test.AbstractRestAssuredIT
import io.restassured.RestAssured
import mykotlinpackage.ScrudBeansSampleApplication
import mykotlinpackage.model.DiscountCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [ScrudBeansSampleApplication::class], webEnvironment = RANDOM_PORT)
class RestErrorsIT : AbstractRestAssuredIT() {

    @Autowired
    lateinit var restTemplateOrig: TestRestTemplate

    val restTemplate: TestRestTemplate by lazy {
        restTemplateOrig.restTemplate.setRequestFactory(BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory()))
        //restTemplateOrig.restTemplate.interceptors.add(RequestResponseLoggingInterceptor())
        restTemplateOrig
    }

    @Test
    fun testNotFound() {
        val error: SimpleErrorResponse = RestAssured.given()
                .spec(defaultSpec())["api/rest/products/invalid"]
                .then()
                .statusCode(404).extract().`as`(SimpleErrorResponse::class.java)
        assertEquals("Not Found", error.message)
        assertEquals(404, error.httpStatusCode)
    }



    @Test
    fun testBadRequestNotNullConstraint() {
        val discountCode = DiscountCode(code = "DISCOUNT_testBadRequestNotNullConstraint")
        val error: SimpleErrorResponse = RestAssured.given()
                .spec(defaultSpec())
                .body(discountCode)
                .post("api/rest/discountCodes")
                .then()
                .statusCode(400).extract().`as`(SimpleErrorResponse::class.java)
        assertEquals("Validation failed", error.message)
        assertEquals(400, error.httpStatusCode)
        assertNotNull(error.validationErrors)
        val violations: Set<ConstraintViolationEntry> = error.validationErrors!!
        assertEquals(1, violations.size)
        val violation = violations.iterator().next()
        assertEquals("must not be null", violation.message)
        assertEquals("percentage", violation.propertyPath)
    }

    /**
     * Test meaningful messages in constraints validation
     */
    @Test
    fun testBadRequestUniqueConstraint() {
        var discountCodeRes = restTemplate.postForEntity("/api/rest/discountCodes",
                DiscountCode(code = "DISCOUNT_testBadRequestUniqueConstraint", percentage = 10), DiscountCode::class.java)
        var discountCode1 = discountCodeRes.body!!
        assertEquals(201, discountCodeRes.statusCodeValue)

        val discountCode2 = discountCode1.copy(id = null)
        val errorRes = restTemplate.postForEntity("/api/rest/discountCodes", discountCode2, SimpleErrorResponse::class.java)
        assertEquals(400, errorRes.statusCodeValue)
        val error = errorRes.body!!
        assertEquals("Validation failed", error.message)
        assertEquals(400, error.httpStatusCode)
        assertNotNull(error.validationErrors)
        assertNotNull(error.validationErrors?.first())
        val violation = error.validationErrors!!.first()
        assertEquals("Unique value not available for property: code", violation.message)
        assertEquals("code", violation.propertyPath)
    }

    /**
     * Test repo's wont merge when trying to create
     */
    @Test
    fun testBadRequestUniqueId() {
        var discountCodeRes = restTemplate.postForEntity("/api/rest/discountCodes",
                DiscountCode(code = "DISCOUNT_testBadRequestUniqueId1", percentage = 6), DiscountCode::class.java)
        var discountCode1 = discountCodeRes.body!!
        assertEquals(HttpStatus.CREATED.value(), discountCodeRes.statusCodeValue)
        val discountCode2 = discountCode1.copy(code = "DISCOUNT_testBadRequestUniqueId2", percentage = 12)
        val errorRes = restTemplate.postForEntity("/api/rest/discountCodes", discountCode2, SimpleErrorResponse::class.java)
        assertEquals(HttpStatus.CONFLICT.value(), errorRes.statusCodeValue)
        val error = errorRes.body!!
        assertEquals(HttpStatus.CONFLICT.value(), error.httpStatusCode)
    }

}