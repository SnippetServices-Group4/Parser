package com.services.group4.parser.controller;

import com.services.group4.parser.model.CommunicationMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnippetController {
  @GetMapping("/test/snippet/communication")
  public CommunicationMessage testSnippetCommunication() {
    return new CommunicationMessage("Parser", "Communication between Parser and Snippet works!");
  }
}
