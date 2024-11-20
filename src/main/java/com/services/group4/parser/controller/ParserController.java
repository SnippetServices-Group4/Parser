package com.services.group4.parser.controller;

import com.services.group4.parser.dto.ValidateResultDto;
import com.services.group4.parser.dto.request.LintingRequestDto;
import com.services.group4.parser.dto.request.ProcessingRequestDto;
import com.services.group4.parser.dto.result.LintingResultDto;
import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.dto.request.TestRequestDto;
import com.services.group4.parser.dto.result.ExecuteResultDto;
import com.services.group4.parser.dto.result.FormattingResultDto;
import com.services.group4.parser.dto.result.ResponseDto;
import com.services.group4.parser.dto.result.TestResponseDto;
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

  @PostMapping("/execute/{snippetId}")
  public ResponseEntity<ResponseDto<ExecuteResultDto>> execute(
      @PathVariable Long snippetId, @RequestBody ProcessingRequestDto request) {
    return parserService.execute(snippetId, request);
  }

  @PostMapping("/format/{snippetId}")
  public ResponseEntity<ResponseDto<FormattingResultDto>> format(
          @PathVariable Long snippetId, @RequestBody FormattingRequestDto request) {
    return parserService
            .format(snippetId, request);
  }

  @PostMapping("/lint/{snippetId}")
  public ResponseEntity<ResponseDto<LintingResultDto>> lint(
          @PathVariable Long snippetId, @RequestBody LintingRequestDto request) {
    return parserService.lint(snippetId, request);
  }

  @PostMapping("/validate/{snippetId}")
  public ResponseEntity<ResponseDto<ValidateResultDto>> analyze(
          @PathVariable Long snippetId, @RequestBody ProcessingRequestDto request) {
    return parserService.validate(snippetId, request);
  }

  @PostMapping("/runTest/{snippetId}")
  public ResponseEntity<ResponseDto<TestResponseDto>> testSnippet(@RequestBody TestRequestDto request, @PathVariable Long snippetId) {
    return parserService.runTest(request, snippetId);
  }

  @GetMapping("/setEnv")
  public ResponseEntity<String> setEnv() {
    snippetService.setEnv();
    return new ResponseEntity<>("Environment set", HttpStatus.OK);
  }
}
