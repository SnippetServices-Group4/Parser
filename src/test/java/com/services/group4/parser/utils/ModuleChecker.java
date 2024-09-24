package com.services.group4.parser.utils;

import org.springframework.web.client.RestTemplate;

public class ModuleChecker {
  public static boolean isSnippetModuleRunning() {
    try {
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getForObject("http://localhost:8080/test/parser/communication", String.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isPermissionModuleRunning() {
    try {
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getForObject("http://localhost:8081/test/parser/communication", String.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
