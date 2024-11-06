package com.services.group4.parser.controller;

import com.services.group4.parser.consumer.lint.TestLintStreamProducer;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestProducerController {
  private final TestLintStreamProducer testLintStreamProducer;

  @Autowired
  public TestProducerController(TestLintStreamProducer testLintStreamProducer) {
    this.testLintStreamProducer = testLintStreamProducer;
  }

  @PostMapping("v1/stream/{userId}")
  public void postMessage(@PathVariable Long userId, @RequestBody Map<String, Object> jsonPayload) {
    testLintStreamProducer.publishEvent(userId, jsonPayload);
  }
}
