package myjavapackage.test;


import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResourceDocument;
import com.github.manosbatsis.scrudbeans.hypermedia.util.JsonApiModelBasedDocumentBuilder;
import com.github.manosbatsis.scrudbeans.test.AbstractRestAssuredIT;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import myjavapackage.ScrudBeansSampleApplication;
import myjavapackage.model.Product;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ScrudBeansSampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JsonApiIT extends AbstractRestAssuredIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonApiIT.class);


    @Test
    public void testCreate() throws Exception {

        String productName = "Test name";

        RequestSpecification requestSpec = jsonApiSpec();
        Product product = new Product(productName, "Test desc", BigDecimal.TEN);
        JsonApiModelResourceDocument<Product, String> document = new JsonApiModelBasedDocumentBuilder<Product, String>("products")
                .withData(product)
                .buildModelDocument();

        // get a document
        Response rs = given().spec(requestSpec)
                .log().all()
                .body(document)
                .post("/api/rest/products");

        // validate response
        rs.then().log().all().assertThat()
                .statusCode(201)
                .body("data.id", notNullValue())
                .body("data.attributes.name", equalTo(productName));

        // search for persisted
        rs = given().spec(requestSpec)
                .log().all()
                .param("name", productName)
                .get("/api/rest/products");

        // validate response
        rs.then().log().all().assertThat()
                .statusCode(200)
                .body("data[0].id", notNullValue())
                .body("data[0].attributes.name", equalTo(productName));

    }

}