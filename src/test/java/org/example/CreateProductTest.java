package org.example;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.pojos.Product;
import org.example.utils.FileUtils;
import org.example.utils.JsonUtils;
import org.example.utils.ProductUtils;
import org.support.RetryAnalyzer;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;


/*
 *  example
 *      curl  --location 'https://api.restful-api.dev/objects' \
 *            --header 'Content-Type: application/json' \
 *            --data '{
 *              "name": "Apple MacBook Pro 16",
 *              "data": {
 *                  "year": 2019,
 *                  "price": 1849.99,
 *                  "color": "black"
 *              }
 *            }' 
 */

public class CreateProductTest {

  private static final Logger logger = LogManager.getLogger(CreateProductTest.class);

  private String CREATE_PRODUCT_JSON_FILE_PATH =
      Constants.JSON_RESOURCES_PATH + Constants.CREATE_OBJECT_JSON_FILENAME;

  private static final String URL = Constants.URI + Constants.OBJECTS_ENDPOINT;


  @Test
  @Severity(SeverityLevel.CRITICAL)
  public void createProductTest() throws IOException {
    SoftAssert softAssert = new SoftAssert();
    JsonNode jsonNode = JsonUtils.readJsonFile(CREATE_PRODUCT_JSON_FILE_PATH);
    // update json
    ObjectNode objectNode = (ObjectNode) jsonNode;
    // update name
    objectNode.put("name", "iBundle Pro");
    // update color
    ObjectNode dataNode = (ObjectNode) objectNode.get("data");
    dataNode.put("color", "midnight gray");
    logger.debug("updated create object json: [{}]", jsonNode.toString());

    Response response = ProductUtils.createProduct(URL, jsonNode.toString());

    // validate response
    assertEquals(response.statusCode(), HttpStatus.SC_OK, "incorrect response status code");

    softAssert.assertNotNull(response.jsonPath().get("id"), "id is null");
    softAssert.assertEquals(
        (Integer) response.jsonPath().get("data.year"),
        (Integer) 2019,
        "year does not match input");

    // extract id
    String id = response.jsonPath().get("id");
    logger.info("created object id: [{}]", id);
    FileUtils.writeToFile(Constants.RESULT_OBJ_ID_FILE, id);

    softAssert.assertAll();
  }

  @Test(retryAnalyzer = RetryAnalyzer.class)
  @Severity(SeverityLevel.CRITICAL)
  public void createProductViaPojoTest() throws IOException {
    SoftAssert softAssert = new SoftAssert();
    ObjectMapper objectMapper = new ObjectMapper();
    String productJson = Files.readString(Paths.get(CREATE_PRODUCT_JSON_FILE_PATH));
    Product product = objectMapper.readValue(productJson, Product.class);
    // builder pattern --> update name, year and color
    product =
        product.toBuilder()
            .name("My name")
            .data(product.getData().toBuilder().year(1999).color("midnight black").build())
            .build();
    logger.debug("json input: [{}]", objectMapper.writeValueAsString(product));

    // create product
    Response response = ProductUtils.createProduct(URL, product);

    // validate response
    assertEquals(response.statusCode(), HttpStatus.SC_OK, "incorrect response status code");

    Product productResponse = response.as(Product.class);
    softAssert.assertNotNull(productResponse.getId(), "id is null");
    softAssert.assertEquals(
        product.getName(), productResponse.getName(), "name does not match input");
    softAssert.assertEquals(
        product.getData(), productResponse.getData(), "data does not match input");

    // extract id
    String id = productResponse.getId();
    logger.info("created object id: [{}]", id);
    FileUtils.writeToFile(Constants.RESULT_OBJ_ID_FILE, id);

    softAssert.assertAll();
  }

  @Test(retryAnalyzer = RetryAnalyzer.class)
  @Severity(SeverityLevel.MINOR)
  public void createProductFailure() {
    SoftAssert softAssert = new SoftAssert();
    
    // create product with no json reequest
    Response response = ProductUtils.createProduct(URL, "");

    // validate response
    softAssert.assertEquals(
        response.getStatusCode(), HttpStatus.SC_BAD_REQUEST, "incorrect status code");
    softAssert.assertNotNull(response.jsonPath().get("error"), "error message missing in response");

    softAssert.assertAll();
  }
}
