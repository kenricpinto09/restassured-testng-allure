package org.example.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtils {
  private static final Logger logger = LogManager.getLogger(FileUtils.class);

  public static void writeToFile(String filePath, String content) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
      writer.write(content);
      writer.newLine(); // insert new line after writing to file
      writer.flush(); // if not added, sometimes newline is not added
    } catch (IOException e) {
      logger.error("exception encountered: ", e);
    }
  }

  public static List<String> readFromFile(String filePath) {
    try {
      return Files.readAllLines(Paths.get(filePath));
    } catch (IOException e) {
      logger.error("Exception encountered: ", e);
      return Collections.emptyList();
    }
  }
}
