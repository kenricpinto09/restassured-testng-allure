package org.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class JsonUtils {

  public static JsonNode readJsonFile(String filePath) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readTree(new File(filePath));
  }
}
