package ibm.cn.application;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class OrdersResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/micro/orders")
          .then()
             .statusCode(200)
             .body(is("Hello RESTEasy"));
    }

}