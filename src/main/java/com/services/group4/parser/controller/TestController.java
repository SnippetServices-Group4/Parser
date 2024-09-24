package com.services.group4.parser.controller;

import com.services.group4.parser.model.CommunicationMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
  @GetMapping("/permission/communication")
  public CommunicationMessage testPermissionCommunication() {
    return new CommunicationMessage("Parser", "Communication from Permission to Parser works!");
  }

  @GetMapping("/snippet/communication")
  public CommunicationMessage testSnippetCommunication() {
    return new CommunicationMessage("Parser", "Communication from Snippet to Parser works!");
  }
}
