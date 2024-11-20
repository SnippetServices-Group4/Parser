package com.services.group4.parser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.services.group4.parser.clients.BucketClient;
import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.dto.request.LintingRequestDto;
import com.services.group4.parser.dto.request.ProcessingRequestDto;
import com.services.group4.parser.dto.result.ExecuteResultDto;
import com.services.group4.parser.dto.result.FormattingResultDto;
import com.services.group4.parser.dto.result.LintingResultDto;
import com.services.group4.parser.dto.result.ResponseDto;
import com.services.group4.parser.services.ParserService;
import com.services.group4.parser.services.SnippetService;
import com.services.group4.parser.utils.TestDtoProvider;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ParserServiceTest {
  @InjectMocks private ParserService parserService;

  @Mock private SnippetService snippetService;

  @Mock private BucketClient bucketClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void shouldExecuteSnippet() {
    ProcessingRequestDto request = TestDtoProvider.getProcessingRequestDto();
    String snippet = "println('Hello World');";
    when(snippetService.getSnippet(1L)).thenReturn(Optional.of(snippet));

    ExecuteResultDto result =
        Objects.requireNonNull(parserService.execute(1L, request).getBody()).data().data();

    assert result.getPrintLog().equals("Hello World");
  }

  @Test
  public void shouldNotExecuteSnippet() {
    ProcessingRequestDto request = TestDtoProvider.getProcessingRequestDto();
    when(snippetService.getSnippet(1L)).thenReturn(Optional.empty());

    ResponseEntity<ResponseDto<ExecuteResultDto>> result = parserService.execute(1L, request);
    assertEquals(HttpStatusCode.valueOf(404), result.getStatusCode());
  }

  @Test
  public void shouldFormatSnippet() {
    FormattingRequestDto request = TestDtoProvider.getFormattingRequestDto();
    String snippet = "let x:number=5;";
    when(snippetService.getSnippet(1L)).thenReturn(Optional.of(snippet));
    when(bucketClient.saveSnippet(eq("snippets"), eq(1L), anyString()))
        .thenReturn(ResponseEntity.ok().build());
    FormattingResultDto result =
        Objects.requireNonNull(parserService.format(1L, request).getBody()).data().data();
    assert result.getFormattedCode().equals("let x : number = 5;\n");
  }

  @Test
  public void shouldNotFormatSnippet() {
    FormattingRequestDto request = TestDtoProvider.getFormattingRequestDto();
    when(snippetService.getSnippet(1L)).thenReturn(Optional.empty());
    ResponseEntity<ResponseDto<FormattingResultDto>> response = parserService.format(1L, request);
    assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
  }

  @Test
  public void shouldLintCamelCaseSnippet() {
    LintingRequestDto request = TestDtoProvider.getAnalyzeCamelCaseRequestDto();
    String snippet = "println(\"Hello World\");";
    when(snippetService.getSnippet(1L)).thenReturn(Optional.of(snippet));
    LintingResultDto result =
        Objects.requireNonNull(parserService.lint(1L, request).getBody()).data().data();
    assert result.getReport().isEmpty();
  }

  @Test
  public void shouldLintSnakeCaseSnippet() {
    LintingRequestDto request = TestDtoProvider.getAnalyzeSnakeCaseRequestDto();
    String snippet = "let my_var : number= 2;";
    when(snippetService.getSnippet(1L)).thenReturn(Optional.of(snippet));
    LintingResultDto result =
        Objects.requireNonNull(parserService.lint(1L, request).getBody()).data().data();
    assert result.getReport().isEmpty();
  }

  @Test
  public void shouldFailLintCamelCaseSnippet() {
    LintingRequestDto request = TestDtoProvider.getAnalyzeCamelCaseRequestDto();
    String snippet = "let my_var : number= 2;";
    when(snippetService.getSnippet(1L)).thenReturn(Optional.of(snippet));
    LintingResultDto result =
        Objects.requireNonNull(parserService.lint(1L, request).getBody()).data().data();
    assert !result.getReport().isEmpty();
  }

  @Test
  public void shouldFailLintSnakeCaseSnippet() {
    LintingRequestDto request = TestDtoProvider.getAnalyzeSnakeCaseRequestDto();
    String snippet = "let myVar : number= 2;";
    when(snippetService.getSnippet(1L)).thenReturn(Optional.of(snippet));
    LintingResultDto result =
        Objects.requireNonNull(parserService.lint(1L, request).getBody()).data().data();
    assert !result.getReport().isEmpty();
  }
}
