package org.example.utils;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProductUtils {

  private static final Logger logger = LogManager.getLogger(ProductUtils.class);

  private ProductUtils() {}

  public static Response createProduct(String url, Object object) {
    Response response =
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(object)
            .when()
            .post(url)
            .then()
            .extract()
            .response();
    logger.debug("create object response: [{}]", response.asPrettyString());
    return response;
  }

  public static Response deleteObject(String url) {
    Response response = given().when().delete(url).thenReturn();
    logger.debug("delete object response: [{}]", response.asPrettyString());
    return response;
  }

  public static Response getSingleProduct(String url) {
    Response response = given().when().get(url).thenReturn();
    logger.debug("get single product response [{}]", response);
    return response;
  }
}
