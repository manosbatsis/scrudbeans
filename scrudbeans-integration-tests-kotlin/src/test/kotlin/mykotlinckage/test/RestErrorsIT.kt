package mykotlinpackage.test

import com.github.manosbatsis.scrudbeans.api.error.ConstraintViolationEntry
import com.github.manosbatsis.scrudbeans.test.AbstractRestAssuredIT
import io.restassured.RestAssured
import mykotlinpackage.ScrudBeansSampleApplication
import mykotlinpackage.model.DiscountCode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [ScrudBeansSampleApplication::class], webEnvironment = RANDOM_PORT)
class RestErrorsIT : AbstractRestAssuredIT() {
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
        val discountCode = DiscountCode(code = "DISCOUNT_1")
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
        var discountCode1 = DiscountCode(code = "DISCOUNT_1", percentage = 10)
        discountCode1 = RestAssured.given()
                .spec(defaultSpec())
                .body(discountCode1)
                .post("/api/rest/discountCodes")
                .then()
                .statusCode(201).extract().`as`(DiscountCode::class.java)
        val discountCode2 = DiscountCode(code = "DISCOUNT_1", percentage = 10)
        val error: SimpleErrorResponse = RestAssured.given()
                .spec(defaultSpec())
                .body(discountCode2)
                .post("/api/rest/discountCodes")
                .then()
                .statusCode(400).extract().`as`(SimpleErrorResponse::class.java)
        assertEquals("Validation failed", error.message)
        assertEquals(400, error.httpStatusCode)
        assertNotNull(error.validationErrors)
        val violations: Set<ConstraintViolationEntry> = error.validationErrors!!
        assertEquals(1, violations.size)
        val violation = violations.iterator().next()
        assertEquals("Unique value not available for property: code", violation.message)
        assertEquals("code", violation.propertyPath)
    }
}