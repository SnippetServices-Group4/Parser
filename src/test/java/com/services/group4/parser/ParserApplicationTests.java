package com.services.group4.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ParserApplicationTests {
  @BeforeAll
  public static void setup() {
    DotenvConfig.loadEnv();
  }

  @Test
  void contextLoads() {}
}
