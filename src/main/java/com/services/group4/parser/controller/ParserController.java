package com.services.group4.parser.controller;

import com.services.group4.parser.common.TestState;
import com.services.group4.parser.common.ValidationState;
import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.dto.request.LintingRequestDto;
import com.services.group4.parser.dto.request.ProcessingRequestDto;
import com.services.group4.parser.dto.request.TestRequestDto;
import com.services.group4.parser.dto.result.ExecuteResultDto;
import com.services.group4.parser.dto.result.FormattingResultDto;
import com.services.group4.parser.dto.result.LintingResultDto;
import com.services.group4.parser.dto.result.ResponseDto;
import com.services.group4.parser.services.ParserService;
import com.services.group4.parser.services.SnippetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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

  @PostMapping("/execute/{snippetId}")
  public ResponseEntity<ResponseDto<ExecuteResultDto>> execute(
      @PathVariable Long snippetId, @RequestBody ProcessingRequestDto request) {
    return parserService.execute(snippetId, request);
  }

  @PostMapping("/format/{snippetId}")
  public ResponseEntity<ResponseDto<FormattingResultDto>> format(
      @PathVariable Long snippetId, @RequestBody FormattingRequestDto request) {
    return parserService.format(snippetId, request);
  }

  @PostMapping("/lint/{snippetId}")
  public ResponseEntity<ResponseDto<LintingResultDto>> lint(
      @PathVariable Long snippetId, @RequestBody LintingRequestDto request) {
    return parserService.lint(snippetId, request);
  }

  @PostMapping("/validate")
  public ResponseEntity<ResponseDto<ValidationState>> analyze(
      @RequestBody ProcessingRequestDto request) {
    return parserService.validate(request);
  }

  @PostMapping("/runTest/{snippetId}")
  public ResponseEntity<ResponseDto<TestState>> testSnippet(
      @RequestBody TestRequestDto request, @PathVariable Long snippetId) {
    return parserService.runTest(request, snippetId);
  }
}
