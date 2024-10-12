package com.services.group4.parser.communication;

import com.services.group4.parser.DotenvConfig;
import com.services.group4.parser.model.CommunicationMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class OuterModulesCommunicationTest {
  @BeforeAll
  public static void setup() {
    DotenvConfig.loadEnv();
  }

  @Configuration
  static class TestConfig {
    @Bean
    public RestTemplate restTemplate() {
      return new RestTemplate();
    }
  }

  @Autowired
  private RestTemplate restTemplate;

  private static final String MODULE_CHECKER_PATH = "com.services.group4.parser.communication.utils.ModuleChecker";

  @Test
  @EnabledIf(MODULE_CHECKER_PATH + "#isSnippetModuleRunning")
  void fromParserToSnippetCommunicationTest() {
    System.out.println("ATTENTION: SNIPPET MODULE MUST BE RUNNING FOR THIS TEST TO PASS!");
    String url = "http://localhost:8080/test/parser/communication";
    CommunicationMessage response = restTemplate.getForObject(url, CommunicationMessage.class);
    assertNotNull(response);
    assertEquals("Snippet", response.source());
    assertEquals("Communication from Parser to Snippet works!", response.message());
  }

  @Test
  @EnabledIf(MODULE_CHECKER_PATH + "#isPermissionModuleRunning")
  void fromParserToPermissionCommunicationTest() {
    System.out.println("ATTENTION: PERMISSION MODULE MUST BE RUNNING FOR THIS TEST TO PASS!");
    String url = "http://localhost:8081/test/parser/communication";
    CommunicationMessage response = restTemplate.getForObject(url, CommunicationMessage.class);
    assertNotNull(response);
    assertEquals("Permission", response.source());
    assertEquals("Communication from Parser to Permission works!", response.message());
  }
}
