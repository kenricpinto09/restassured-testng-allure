package org.support;

import io.restassured.response.Response;
import java.util.List;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Constants;
import org.example.utils.FileUtils;
import org.example.utils.ProductUtils;
import org.testng.annotations.AfterSuite;

public class Supporter {

  private static final Logger logger = LogManager.getLogger(Supporter.class);

  private static final String DELETE_OBJECT_URL =
      Constants.URI + Constants.OBJECTS_ENDPOINT + "/<id>";

  @AfterSuite
  public void AfterSuite() {
    logger.info("********** Running Cleanup **********");
    List<String> idList = FileUtils.readFromFile(Constants.RESULT_OBJ_ID_FILE);
    for (String id : idList) {
      logger.debug("deleting product: [{}]", id);
      Response response = ProductUtils.deleteObject(DELETE_OBJECT_URL.replace("<id>", id));
      if (response.getStatusCode() != HttpStatus.SC_OK) {
        logger.error("data cleanup for id: [{}] unsuccessful", id);
      }
    }
    logger.info("********** Cleanup Complete **********");
  }
}
