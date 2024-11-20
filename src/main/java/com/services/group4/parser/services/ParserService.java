package com.services.group4.parser.services;

import com.services.group4.parser.clients.BucketClient;
import com.services.group4.parser.common.Language;
import com.services.group4.parser.common.TestState;
import com.services.group4.parser.common.ValidationState;
import com.services.group4.parser.common.response.FullResponse;
import com.services.group4.parser.dto.ValidateResultDto;
import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.dto.request.LintingRequestDto;
import com.services.group4.parser.dto.request.ProcessingRequestDto;
import com.services.group4.parser.dto.request.TestRequestDto;
import com.services.group4.parser.dto.result.*;
import com.services.group4.parser.services.adapter.FormatConfigAdapter;
import com.services.group4.parser.services.adapter.LintConfigAdapter;
import com.services.group4.parser.services.utils.OutputListString;
import input.InputHandler;
import input.InputQueue;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import output.OutputReport;
import output.OutputResult;
import output.OutputString;
import runner.Runner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;

@Service
public class ParserService {
  List<Language> languages;
  SnippetService snippetService;
  BucketClient bucketClient;
  private final String container = "snippets";

  @Autowired
  public ParserService(SnippetService snippetService, BucketClient bucketClient) {
    //TODO: move to another class
    List<String> versions = List.of("1.0", "1.1");
    this.languages = List.of(new Language("printscript", versions));
    this.snippetService = snippetService;
    this.bucketClient = bucketClient;
  }

  private void validateLanguage(String language, String version) {
    if (languages.stream()
            .noneMatch(l -> l.getLangName().equals(language) && l.getVersion().contains(version))) {
      throw new NoSuchElementException("Language not supported");
    }
  }

  //TODO: execute should switch on language
  public ResponseEntity<ResponseDto<ExecuteResultDto>> execute(Long snippetId, ProcessingRequestDto request) {
    String language = request.getLanguage().toLowerCase();
    String version = request.getVersion();

    validateLanguage(language, version);

    Optional<String> snippet = snippetService.getSnippet(snippetId);
      return snippet.map(s -> getExecuteResultDto(s, version)).orElseGet(() -> FullResponse.create("Snippet not found", "executeResult", null, HttpStatus.NOT_FOUND));
  }

  private static ResponseEntity<ResponseDto<ExecuteResultDto>> getExecuteResultDto(String snippet, String version) {
    return getExecuteResultDto(snippet, version, List.of());
  }

  @NotNull
  private static ResponseEntity<ResponseDto<ExecuteResultDto>> getExecuteResultDto(String snippet, String version, List<String> inputs) {
    Runner runner = new Runner();

    InputStream stream = new ByteArrayInputStream(snippet.getBytes());

    OutputResult<String> printLog = new OutputString();
    OutputResult<String> errorLog = new OutputString();
    Queue<String> inputQueue = new LinkedList<>(inputs);
    InputHandler inputHandler = new InputQueue(inputQueue);

    try {
      runner.execute(stream, version, printLog, errorLog, inputHandler);
      return FullResponse.create("Snippet executed successfully", "executeResult", new ExecuteResultDto(printLog.getResult(), errorLog.getResult()), HttpStatus.OK);
    }
    catch (Exception e) {
      return FullResponse.create("Snippet execution failed", "executeResult", null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public ResponseEntity<ResponseDto<TestResponseDto>> runTest(TestRequestDto request, Long snippetId) {
    Optional<String> snippet = snippetService.getSnippet(snippetId);

    if (snippet.isEmpty()) {
      return FullResponse.create("Snippet not found", "executedTest", null, HttpStatus.NOT_FOUND);
    }

    Runner runner = new Runner();

    InputStream stream = new ByteArrayInputStream(snippet.get().getBytes());

    OutputListString testOutput = new OutputListString();
    OutputResult<String> errorOutput = new OutputString();
    Queue<String> inputQueue = new LinkedList<>(request.getInputs());
    InputHandler inputHandler = new InputQueue(inputQueue);

    try {
      runner.execute(stream, request.getVersion(), testOutput, errorOutput, inputHandler);

      boolean success = testOutput.getListString().equals(request.getOutputs());

      return FullResponse.create("Test ran successfully", "executedTest",
              new TestResponseDto(snippetId, request.getTestId(), success ? TestState.PASSED : TestState.FAILED), HttpStatus.OK);
    }
    catch (Exception e) {
      return FullResponse.create("Something went wrong when executing the tests", "executedTest", null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public ResponseEntity<ResponseDto<FormattingResultDto>> format(Long snippetId, FormattingRequestDto request) {
    String language = request.getLanguage().toLowerCase();
    String version = request.getVersion();

    validateLanguage(language, version);

    Optional<String> snippet = snippetService.getSnippet(snippetId);

    if (snippet.isEmpty()) {
      return FullResponse.create("Snippet not found", "formatResult", null, HttpStatus.NOT_FOUND);
    }

    FormatConfigAdapter formatConfigAdapter = new FormatConfigAdapter();
    String rules = formatConfigAdapter.adaptFormatConfig(request.getFormatRules());

    try {
      String output = format(snippet.get(), version, rules);
      bucketClient.saveSnippet(container, snippetId, output);
      return FullResponse.create("Snippet formatting executed successfully", "formatResult", new FormattingResultDto(output, language, version, rules), HttpStatus.OK);
    }
    catch (Exception e) {
      return FullResponse.create("Snippet formatting failed", "formatResult", null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private String format(String snippet, String version, String formatRules) throws IOException {
    Runner runner = new Runner();
    InputStream stream = new ByteArrayInputStream(snippet.getBytes());
    OutputResult<String> output = new OutputString();
    runner.format(stream, version, formatRules, output);

    return output.getResult();
  }

  public ResponseEntity<ResponseDto<LintingResultDto>> lint(Long snippetId, LintingRequestDto request) {
    String language = request.getLanguage().toLowerCase();
    String version = request.getVersion();

    validateLanguage(language, version);

    Optional<String> snippet = snippetService.getSnippet(snippetId);

    if (snippet.isEmpty()) {
        return FullResponse.create("Snippet not found", "lintResult", null, HttpStatus.NOT_FOUND);
    }

    LintConfigAdapter lintConfigAdapter = new LintConfigAdapter();
    String rules = lintConfigAdapter.adaptLintConfig(request.getLintRules());

    try {
      OutputReport report = lint(snippet.get(), version, rules);
      return FullResponse.create("Linting executed successfully", "lintResult", new LintingResultDto(report.getFullReport().getReports(), language, version, rules), HttpStatus.OK);
    }
    catch (Exception e) {
      return FullResponse.create("Linting failed", "lintResult", null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private OutputReport lint(String snippet, String version, String lintRules) {
    Runner runner = new Runner();
    InputStream stream = new ByteArrayInputStream(snippet.getBytes());
    OutputReport output = new OutputReport();

    runner.analyze(stream, version, lintRules, output);

    return output;
  }

  public ResponseEntity<ResponseDto<ValidateResultDto>> validate(Long snippetId, ProcessingRequestDto request) {
    String language = request.getLanguage().toLowerCase();
    String version = request.getVersion();

    validateLanguage(language, version);

    Optional<String> snippet = snippetService.getSnippet(snippetId);

    if (snippet.isEmpty()) {
      return FullResponse.create("Snippet not found", "validationResult", null, HttpStatus.NOT_FOUND);
    }

    Runner runner = new Runner();
    InputStream stream = new ByteArrayInputStream(snippet.get().getBytes());
    ValidationState state;
    String report;

    try {
      runner.validate(stream, version);
      state = ValidationState.VALID;
      report = "Validation successful";
    } catch (Exception e) {
      state = ValidationState.INVALID;
      report = e.getMessage();
    }

    return FullResponse.create("Validation executed", "validationResult", new ValidateResultDto(report, state, language, version), HttpStatus.OK);
  }
}

