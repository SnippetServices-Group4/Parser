package com.services.group4.parser.controller;

import com.services.group4.parser.model.CommunicationMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PermissionController {
  @GetMapping("/test/permission/communication")
  public CommunicationMessage testPermissionCommunication() {
    return new CommunicationMessage("Parser", "Communication between Parser and Permission works!");
  }
}
