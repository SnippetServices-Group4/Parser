package com.services.group4.parser.services;

import com.services.group4.parser.clients.BucketClient;
import com.services.group4.parser.common.Language;
import com.services.group4.parser.common.TestState;
import com.services.group4.parser.common.ValidationState;
import com.services.group4.parser.common.response.FullResponse;
import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.dto.request.LintingRequestDto;
import com.services.group4.parser.dto.request.ProcessingRequestDto;
import com.services.group4.parser.dto.request.TestRequestDto;
import com.services.group4.parser.dto.result.ExecuteResultDto;
import com.services.group4.parser.dto.result.FormattingResultDto;
import com.services.group4.parser.dto.result.LintingResultDto;
import com.services.group4.parser.dto.result.ResponseDto;
import com.services.group4.parser.services.adapter.FormatConfigAdapter;
import com.services.group4.parser.services.adapter.LintConfigAdapter;
import com.services.group4.parser.services.utils.OutputListString;
import input.InputHandler;
import input.InputQueue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import output.OutputReport;
import output.OutputResult;
import output.OutputString;
import runner.Runner;

@Slf4j
@Service
public class ParserService {
  List<Language> languages;
  SnippetService snippetService;
  BucketClient bucketClient;
  private final String container = "snippets";

  @Autowired
  public ParserService(SnippetService snippetService, BucketClient bucketClient) {
    // TODO: move to another class
    List<String> versions = List.of("1.0", "1.1");
    this.languages = List.of(new Language("printscript", versions));
    this.snippetService = snippetService;
    this.bucketClient = bucketClient;
  }

  private void validateLanguage(String language, String version) {
    log.info("Validating snippet language and version: {}", language);
    if (languages.stream()
        .noneMatch(l -> l.getLangName().equals(language) && l.getVersion().contains(version))) {
      log.info("Language or version not supported: {} , {}", language, version);
      throw new NoSuchElementException("Language not supported");
    }
    log.info("Language and version validated: {} {}", language, version);
  }

  // TODO: execute should switch on language
  public ResponseEntity<ResponseDto<ExecuteResultDto>> execute(
      Long snippetId, ProcessingRequestDto request) {
    String language = request.language().toLowerCase();
    String version = request.version();

    validateLanguage(language, version);

    Optional<String> snippet = snippetService.getSnippet(snippetId);
    return snippet
        .map(s -> getExecuteResultDto(snippetId, s, version, List.of()))
        .orElseGet(
            () ->
                FullResponse.create(
                    "Snippet not found", "executeResult", null, HttpStatus.NOT_FOUND));
  }

  @NotNull
  private static ResponseEntity<ResponseDto<ExecuteResultDto>> getExecuteResultDto(
      Long snippetId, String snippet, String version, List<String> inputs) {
    Runner runner = new Runner();

    InputStream stream = new ByteArrayInputStream(snippet.getBytes());

    OutputResult<String> printLog = new OutputString();
    OutputResult<String> errorLog = new OutputString();
    Queue<String> inputQueue = new LinkedList<>(inputs);
    InputHandler inputHandler = new InputQueue(inputQueue);

    try {
      log.info("Executing snippet with id: {}", snippetId);
      runner.execute(stream, version, printLog, errorLog, inputHandler);
      log.info("Snippet executed successfully: {}", snippetId);
      log.debug("Print log: {}, Error log: {}", printLog.getResult(), errorLog.getResult());

      return FullResponse.create(
          "Snippet executed successfully",
          "executeResult",
          new ExecuteResultDto(snippetId, printLog.getResult(), errorLog.getResult()),
          HttpStatus.OK);
    } catch (Exception e) {
      log.error("Snippet execution failed: {}", snippetId);
      log.debug("Error log: {}", e.getMessage());
      return FullResponse.create(
          "Snippet execution failed", "executeResult", null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public ResponseEntity<ResponseDto<TestState>> runTest(TestRequestDto request, Long snippetId) {
    Optional<String> snippet = snippetService.getSnippet(snippetId);

    if (snippet.isEmpty()) {
      return FullResponse.create("Snippet not found", "executedTest", null, HttpStatus.NOT_FOUND);
    }

    Runner runner = new Runner();

    InputStream stream = new ByteArrayInputStream(snippet.get().getBytes());

    OutputListString testOutput = new OutputListString();
    OutputResult<String> errorOutput = new OutputString();
    Queue<String> inputQueue = new LinkedList<>(request.inputs());
    InputHandler inputHandler = new InputQueue(inputQueue);

    log.info("Trying to run tests for snippet with id: {}", snippetId);
    try {
      runner.execute(stream, request.version(), testOutput, errorOutput, inputHandler);

      boolean success = testOutput.getListString().equals(request.outputs());

      return FullResponse.create(
          "Test ran successfully",
          "testState",
          success ? TestState.PASSED : TestState.FAILED,
          HttpStatus.OK);
    } catch (Exception e) {
      return FullResponse.create(
          "Something went wrong when executing the tests",
          "testState",
          null,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public ResponseEntity<ResponseDto<FormattingResultDto>> format(
      Long snippetId, FormattingRequestDto request) {
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
      log.info("Trying to format snippet with id: {}", snippetId);
      String output = format(snippet.get(), version, rules);
      log.info("Snippet formatted successfully: {}", snippetId);
      log.debug(
          "Formatted snippet with id {} using rules: {}, version: {}", snippetId, rules, version);

      log.info("Trying to save formatted snippet in bucket");
      bucketClient.saveSnippet(container, snippetId, output);
      log.info("Formatted snippet successfully saved in bucket");
      return FullResponse.create(
          "Snippet formatting executed successfully",
          "formatResult",
          new FormattingResultDto(snippetId, output, language, version, rules),
          HttpStatus.OK);
    } catch (Exception e) {
      log.error("Snippet formatting with id: {}", snippetId + "failed");
      log.debug("Error log: {}", e.getMessage());
      return FullResponse.create(
          "Snippet formatting failed", "formatResult", null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private String format(String snippet, String version, String formatRules) throws IOException {
    Runner runner = new Runner();
    InputStream stream = new ByteArrayInputStream(snippet.getBytes());
    OutputResult<String> output = new OutputString();
    runner.format(stream, version, formatRules, output);

    return output.getResult();
  }

  public ResponseEntity<ResponseDto<LintingResultDto>> lint(
      Long snippetId, LintingRequestDto request) {
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
      log.info("Trying to lint snippet with id: {}", snippetId);
      OutputReport report = lint(snippet.get(), version, rules);
      log.info("Snippet linted successfully: {}", snippetId);
      return FullResponse.create(
          "Linting executed successfully",
          "lintResult",
          new LintingResultDto(
              snippetId, report.getFullReport().getReports(), language, version, rules),
          HttpStatus.OK);
    } catch (Exception e) {
      log.error("Snippet linting with id: {}", snippetId + "failed");
      log.debug("Error log: {}", e.getMessage());
      return FullResponse.create(
          "Linting failed",
          "lintResult",
          new LintingResultDto(snippetId, List.of(), language, version, rules),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private OutputReport lint(String snippet, String version, String lintRules) {
    Runner runner = new Runner();
    InputStream stream = new ByteArrayInputStream(snippet.getBytes());
    OutputReport output = new OutputReport();

    runner.analyze(stream, version, lintRules, output);

    return output;
  }

  public ResponseEntity<ResponseDto<ValidationState>> validate(ProcessingRequestDto request) {
    String language = request.language().toLowerCase();
    String version = request.version();

    validateLanguage(language, version);

    String content = request.content();

    Runner runner = new Runner();
    InputStream stream = new ByteArrayInputStream(content.getBytes());
    ValidationState state;
    String report;

    log.info("Trying to validate snippet");
    try {
      runner.validate(stream, version);
      state = ValidationState.VALID;
      report = "successfully";
    } catch (Exception e) {
      state = ValidationState.INVALID;
      report = e.getMessage();
      log.debug("Snippet validation failed: {}", e.getMessage());
    }

    log.info("Snippet validated: {}", state);
    return FullResponse.create(
        "Validation executed: " + report, "validationResult", state, HttpStatus.OK);
  }
}
