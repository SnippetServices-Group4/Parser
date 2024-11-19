package com.services.group4.parser.services;

import com.services.group4.parser.clients.BucketClient;
import com.services.group4.parser.common.Language;
import com.services.group4.parser.common.TestState;
import com.services.group4.parser.common.response.DataTuple;
import com.services.group4.parser.common.response.FullResponse;
import com.services.group4.parser.dto.request.ExecuteRequestDto;
import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.dto.request.TestRequestDto;
import com.services.group4.parser.dto.result.ExecuteResultDto;
import com.services.group4.parser.dto.result.FormattingResultDto;
import com.services.group4.parser.dto.result.ResponseDto;
import com.services.group4.parser.services.adapter.FormatConfigAdapter;
import com.services.group4.parser.services.utils.OutputListString;
import input.InputHandler;
import input.InputQueue;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import output.OutputResult;
import output.OutputString;
import runner.Runner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

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
  public Optional<ExecuteResultDto> execute(Long snippetId, ExecuteRequestDto request) {
    String language = request.getLanguage().toLowerCase();
    String version = request.getVersion();

    validateLanguage(language, version);

    String snippet = snippetService.getSnippet(snippetId);

    return Optional.of(getExecuteResultDto(snippet, version));
  }

  private static ExecuteResultDto getExecuteResultDto(String snippet, String version) {
    return getExecuteResultDto(snippet, version, List.of());
  }

  @NotNull
  private static ExecuteResultDto getExecuteResultDto(String snippet, String version, List<String> inputs) {
    if (snippet.isEmpty()) {
      throw new NoSuchElementException("Snippet not found");
    }

    Runner runner = new Runner();

    InputStream stream = new ByteArrayInputStream(snippet.getBytes());

    OutputResult<String> printLog = new OutputString();
    OutputResult<String> errorLog = new OutputString();
    Queue<String> inputQueue = new LinkedList<>(inputs);
    InputHandler inputHandler = new InputQueue(inputQueue);

    runner.execute(stream, version, printLog, errorLog, inputHandler);

    return new ExecuteResultDto(printLog.getResult(), errorLog.getResult());
  }

  public ResponseEntity<ResponseDto<TestState>> runTest(TestRequestDto request) {
    String snippet = snippetService.getSnippet(request.getSnippetId());

    Runner runner = new Runner();

    InputStream stream = new ByteArrayInputStream(snippet.getBytes());

    OutputListString testOutput = new OutputListString();
    OutputResult<String> errorOutput = new OutputString();
    Queue<String> inputQueue = new LinkedList<>(request.getInputs());
    InputHandler inputHandler = new InputQueue(inputQueue);

    runner.execute(stream, request.getVersion(), testOutput, errorOutput, inputHandler);

    boolean success = testOutput.getListString().equals(request.getOutputs());

    return FullResponse.create( "Test ran successfully",
        "testState", success ? TestState.PASSED : TestState.FAILED, HttpStatus.OK);
  }

  public Optional<FormattingResultDto> format(Long snippetId, FormattingRequestDto request) {
    String language = request.getLanguage().toLowerCase();
    String version = request.getVersion();

    validateLanguage(language, version);

    String snippet = snippetService.getSnippet(snippetId);

    FormatConfigAdapter formatConfigAdapter = new FormatConfigAdapter();
    String rules = formatConfigAdapter.adaptFormatConfig(request.getFormatRules());

    String output = getFormattingResultDto(snippet, version, rules);

    bucketClient.saveSnippet(container, snippetId, output);
    return Optional.of(new FormattingResultDto(output, language, version, rules));
  }

  private String getFormattingResultDto(String snippet, String version, String formatRules) {
    if (snippet.isEmpty()) {
      throw new NoSuchElementException("Snippet not found");
    }

    Runner runner = new Runner();
    InputStream stream = new ByteArrayInputStream(snippet.getBytes());
    OutputResult<String> output = new OutputString();
    try{
      runner.format(stream, version, formatRules, output);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return output.getResult();
  }
}

