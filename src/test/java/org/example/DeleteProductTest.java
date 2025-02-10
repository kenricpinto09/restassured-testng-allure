package org.example;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.utils.ProductUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class DeleteProductTest {
  private static final Logger logger = LogManager.getLogger(DeleteProductTest.class);

  private static final String DELETE_OBJECT_URL =
      Constants.URI + Constants.OBJECTS_ENDPOINT + "/<id>";

  private static final String CREATE_OBJECT_URL = Constants.URI + Constants.OBJECTS_ENDPOINT;

  private String CREATE_OBJECT_JSON_FILE_PATH =
      Constants.JSON_RESOURCES_PATH + Constants.CREATE_OBJECT_JSON_FILENAME;

  @Test
  @Severity(SeverityLevel.NORMAL)
  public void deleteProductTest() throws IOException {
    SoftAssert softAssert = new SoftAssert();

    String productJson = Files.readString(Paths.get(CREATE_OBJECT_JSON_FILE_PATH));
    Response createObjResponse = ProductUtils.createProduct(CREATE_OBJECT_URL, productJson);
    Assert.assertEquals(
        createObjResponse.getStatusCode(), HttpStatus.SC_OK, "create product failure");

    String id = createObjResponse.getBody().jsonPath().getString("id");
    logger.info("product id to be deleted: [{}]", id);
    Response deleteObjResponse = ProductUtils.deleteObject(DELETE_OBJECT_URL.replace("<id>", id));

    // validate response
    softAssert.assertEquals(
        deleteObjResponse.getStatusCode(), HttpStatus.SC_OK, "incorrect status code");
    String responseMessage = deleteObjResponse.jsonPath().get("message");
    softAssert.assertNotNull(responseMessage, "message in response body is null");
    softAssert.assertTrue(
        responseMessage.contains(id), "delete id is not present in the response body");
    softAssert.assertAll();
  }

  @Test
  @Severity(SeverityLevel.MINOR)
  public void deleteAlreadyDeletedProductTest() throws IOException {
    SoftAssert softAssert = new SoftAssert();

    String productJson = Files.readString(Paths.get(CREATE_OBJECT_JSON_FILE_PATH));
    Response createObjResponse = ProductUtils.createProduct(CREATE_OBJECT_URL, productJson);
    Assert.assertEquals(
        createObjResponse.getStatusCode(), HttpStatus.SC_OK, "create product failure");

    String id = createObjResponse.getBody().jsonPath().getString("id");
    logger.info("product id to be deleted: [{}]", id);

    String updatedUrl = DELETE_OBJECT_URL.replace("<id>", id);

    // delete the same object twice
    Response response = ProductUtils.deleteObject(updatedUrl);
    response = ProductUtils.deleteObject(updatedUrl);

    // validate response
    softAssert.assertEquals(
        response.getStatusCode(), HttpStatus.SC_NOT_FOUND, "incorrect status code");
    String responseMessage = response.jsonPath().get("error");
    softAssert.assertNotNull(responseMessage, "message in response body is null");
    softAssert.assertTrue(
        responseMessage.contains(id), "delete id is not present in the response body");
    softAssert.assertAll();
  }
}
