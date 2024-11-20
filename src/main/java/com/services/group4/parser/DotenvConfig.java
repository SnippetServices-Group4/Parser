package com.services.group4.parser;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DotenvConfig {
  public static void loadEnv() {
    if (Files.exists(Paths.get(".dockerenv"))) {
      // Running in Docker, does not search for environment variables
      return;
    }

    if (Files.exists(Paths.get(".env"))) {
      Dotenv dotenv = Dotenv.load();
      dotenv
          .entries()
          .forEach(
              entry -> {
                if (System.getProperty(entry.getKey()) == null) {
                  System.setProperty(entry.getKey(), entry.getValue());
                }
              });
    } else {
      System.getenv()
          .forEach(
              (key, value) -> {
                if (System.getProperty(key) == null) {
                  System.setProperty(key, value);
                }
              });
    }
  }
}
