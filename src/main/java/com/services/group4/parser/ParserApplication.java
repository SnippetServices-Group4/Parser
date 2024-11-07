package com.services.group4.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients
public class ParserApplication {

  public static void main(String[] args) {
    DotenvConfig.loadEnv();
    SpringApplication.run(ParserApplication.class, args);
  }
}
