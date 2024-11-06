package com.services.group4.parser.controller;

import com.services.group4.parser.consumer.lint.TestLintStreamProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestProducerController {
  private final TestLintStreamProducer testLintStreamProducer;

  @Autowired
  public TestProducerController(TestLintStreamProducer testLintStreamProducer) {
    this.testLintStreamProducer = testLintStreamProducer;
  }

  @PostMapping("v1/stream/{message}")
  public void postMessage(@PathVariable String message) {
    testLintStreamProducer.publishEvent(message);
  }
}
