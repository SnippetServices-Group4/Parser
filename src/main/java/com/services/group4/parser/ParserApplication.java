package com.services.group4.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ParserApplication {

  public static void main(String[] args) {
    DotenvConfig.loadEnv();
    SpringApplication.run(ParserApplication.class, args);
  }
}
