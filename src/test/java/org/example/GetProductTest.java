package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.pojos.Product;
import org.example.utils.FileUtils;
import org.example.utils.ProductUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

// curl --location 'https://api.restful-api.dev/objects/ff808181932badb60194d35c633b4b2f'

public class GetProductTest {

  private static final Logger logger = LogManager.getLogger(GetProductTest.class);

  private static final String GET_DELETE_PRODUCT_URL =
      Constants.URI + Constants.OBJECTS_ENDPOINT + "/<id>";

  private static final String CREATE_PRODUCT_URL = Constants.URI + Constants.OBJECTS_ENDPOINT;

  private String CREATE_PRODUCT_JSON_FILE_PATH =
      Constants.JSON_RESOURCES_PATH + Constants.CREATE_OBJECT_JSON_FILENAME;

  @Test
  @Severity(SeverityLevel.CRITICAL)
  public void getProductTest() throws IOException {
    SoftAssert softAssert = new SoftAssert();

    // create product
    String productJson = Files.readString(Paths.get(CREATE_PRODUCT_JSON_FILE_PATH));
    Product createProduct = new ObjectMapper().readValue(productJson, Product.class);
    Response createObjResponse = ProductUtils.createProduct(CREATE_PRODUCT_URL, createProduct);
    Assert.assertEquals(
        createObjResponse.getStatusCode(), HttpStatus.SC_OK, "create product failure");

    // extract product id
    String id = createObjResponse.getBody().jsonPath().getString("id");
    logger.info("created product id: [{}]", id);
    FileUtils.writeToFile(Constants.RESULT_OBJ_ID_FILE, id);

    // get product
    Response response = ProductUtils.getSingleProduct(GET_DELETE_PRODUCT_URL.replace("<id>", id));

    // validate response
    Assert.assertEquals(
        response.getStatusCode(), HttpStatus.SC_OK, "get product status code mismtach");

    Product responseProduct = response.as(Product.class);
    softAssert.assertEquals(responseProduct.getId(), id, "incorrect product id");
    softAssert.assertEquals(
        responseProduct.getName(), createProduct.getName(), "name does not match input");
    softAssert.assertEquals(
        responseProduct.getData(), createProduct.getData(), "data does not match input");
    softAssert.assertAll();
  }

  @Test
  @Severity(SeverityLevel.MINOR)
  public void getNonexistentProductTest() {
    SoftAssert softAssert = new SoftAssert();
    String id = RandomStringUtils.randomAlphanumeric(32);

    // get product
    Response response = ProductUtils.getSingleProduct(GET_DELETE_PRODUCT_URL.replace("<id>", id));

    // validate response
    softAssert.assertEquals(
        response.getStatusCode(), HttpStatus.SC_NOT_FOUND, "incorrect get product status code");
    String responseMessage = response.jsonPath().get("error");
    softAssert.assertNotNull(responseMessage, "message in response body is null");
    softAssert.assertTrue(
        responseMessage.contains(id), "delete id is not present in the response body");
    softAssert.assertAll();
  }

  @Test
  @Severity(SeverityLevel.NORMAL)
  public void getDeletedProductTest() throws IOException {
    SoftAssert softAssert = new SoftAssert();

    // create product
    String productJson = Files.readString(Paths.get(CREATE_PRODUCT_JSON_FILE_PATH));
    Product createProduct = new ObjectMapper().readValue(productJson, Product.class);
    Response createObjResponse = ProductUtils.createProduct(CREATE_PRODUCT_URL, createProduct);
    Assert.assertEquals(
        createObjResponse.getStatusCode(), HttpStatus.SC_OK, "create product failure");

    // extract product id
    String id = createObjResponse.getBody().jsonPath().getString("id");
    logger.info("created product id: [{}]", id);
    FileUtils.writeToFile(Constants.RESULT_OBJ_ID_FILE, id);

    // delete product
    Response deleteProductResponse =
        ProductUtils.deleteObject(GET_DELETE_PRODUCT_URL.replace("<id>", id));
    Assert.assertEquals(
        deleteProductResponse.getStatusCode(), HttpStatus.SC_OK, "delete product failure");

    // get product
    Response response = ProductUtils.getSingleProduct(GET_DELETE_PRODUCT_URL.replace("<id>", id));
    // validate response
    softAssert.assertEquals(
        response.getStatusCode(), HttpStatus.SC_NOT_FOUND, "incorrect get product status code");
    String responseMessage = response.jsonPath().get("error");
    softAssert.assertNotNull(responseMessage, "message in response body is null");
    softAssert.assertTrue(
        responseMessage.contains(id), "delete id is not present in the response body");
    softAssert.assertAll();
  }
}
