package com.services.group4.parser.controller;

import com.services.group4.parser.dto.ExecuteRequestDto;
import com.services.group4.parser.dto.ExecuteResultDto;
import com.services.group4.parser.services.ParserService;
import com.services.group4.parser.services.SnippetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parsers")
public class ParserController {
  private final ParserService parserService;
  private final SnippetService snippetService;

  @Autowired
  public ParserController(ParserService parserService, SnippetService snippetService) {
    this.parserService = parserService;
    this.snippetService = snippetService;
  }

  @PostMapping("/{snippetId}")
  public ResponseEntity<ExecuteResultDto> execute(
      @PathVariable Long snippetId, @RequestBody ExecuteRequestDto request) {
    return parserService
        .execute(snippetId, request)
        .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/setEnv")
  public ResponseEntity<String> setEnv() {
    snippetService.setEnv();
    return new ResponseEntity<>("Environment set", HttpStatus.OK);
  }
}
