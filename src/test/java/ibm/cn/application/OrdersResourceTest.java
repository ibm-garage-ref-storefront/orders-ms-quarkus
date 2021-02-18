package ibm.cn.application;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class OrdersResourceTest {

    @Test
    public void testOrdersEndpoint() {
        given()
          .when().get("/micro/orders/resource")
          .then()
             .statusCode(200)
             .body(is("OrdersResource response"));
    }

}