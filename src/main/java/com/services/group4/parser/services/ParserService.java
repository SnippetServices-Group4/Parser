package com.services.group4.parser.services;

import com.services.group4.parser.clients.BucketClient;
import com.services.group4.parser.common.Language;
import com.services.group4.parser.dto.request.ExecuteRequestDto;
import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.dto.request.LintingRequestDto;
import com.services.group4.parser.dto.result.ExecuteResultDto;
import com.services.group4.parser.dto.result.FormattingResultDto;
import com.services.group4.parser.dto.result.LintingResultDto;
import com.services.group4.parser.services.adapter.FormatConfigAdapter;
import com.services.group4.parser.services.adapter.LintConfigAdapter;
import input.InputHandler;
import input.InputQueue;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import output.OutputReport;
import output.OutputResult;
import output.OutputString;
import runner.Runner;

import java.io.ByteArrayInputStream;
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
  public Optional<ExecuteResultDto> execute(Long snippetId, ExecuteRequestDto request) {
    String language = request.getLanguage().toLowerCase();
    String version = request.getVersion();

    validateLanguage(language, version);

    Optional<String> snippet = snippetService.getSnippet(snippetId);

    return Optional.of(getExecuteResultDto(snippet, version));
  }

  @NotNull
  private static ExecuteResultDto getExecuteResultDto(Optional<String> snippet, String version) {
    if (snippet.isEmpty()) {
      throw new NoSuchElementException("Snippet not found");
    }

    Runner runner = new Runner();

    InputStream stream = new ByteArrayInputStream(snippet.get().getBytes());

    OutputResult<String> printLog = new OutputString();
    OutputResult<String> errorLog = new OutputString();
    Queue<String> inputQueue = new LinkedList<>();
    InputHandler inputHandler = new InputQueue(inputQueue);

    runner.execute(stream, version, printLog, errorLog, inputHandler);

    return new ExecuteResultDto(printLog.getResult(), errorLog.getResult());
  }

  public Optional<FormattingResultDto> format(Long snippetId, FormattingRequestDto request) {
    String language = request.getLanguage().toLowerCase();
    String version = request.getVersion();

    validateLanguage(language, version);

    Optional<String> snippet = snippetService.getSnippet(snippetId);

    FormatConfigAdapter formatConfigAdapter = new FormatConfigAdapter();
    String rules = formatConfigAdapter.adaptFormatConfig(request.getFormatRules());

    String output = format(snippet, version, rules);

    bucketClient.saveSnippet(container, snippetId, output);
    return Optional.of(new FormattingResultDto(output, language, version, rules));
  }

  private String format(Optional<String> snippet, String version, String formatRules) {
    if (snippet.isEmpty()) {
      throw new NoSuchElementException("Snippet not found");
    }

    Runner runner = new Runner();
    InputStream stream = new ByteArrayInputStream(snippet.get().getBytes());
    OutputResult<String> output = new OutputString();
    try{
      runner.format(stream, version, formatRules, output);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return output.getResult();
  }

  public Optional<LintingResultDto> lint(Long snippetId, LintingRequestDto request) {
    String language = request.getLanguage().toLowerCase();
    String version = request.getVersion();

    validateLanguage(language, version);

    Optional<String> snippet = snippetService.getSnippet(snippetId);

    LintConfigAdapter lintConfigAdapter = new LintConfigAdapter();
    String rules = lintConfigAdapter.adaptLintConfig(request.getLintRules());

    OutputReport report = lint(snippet, version, rules);

    return Optional.of(new LintingResultDto(report.getFullReport().getReports(), language, version, rules));
  }

  private OutputReport lint(Optional<String> snippet, String version, String lintRules) {
    if (snippet.isEmpty()) {
      throw new NoSuchElementException("Snippet not found");
    }

    Runner runner = new Runner();
    InputStream stream = new ByteArrayInputStream(snippet.get().getBytes());
    OutputReport output = new OutputReport();

    runner.analyze(stream, version, lintRules, output);

    return output;
  }

}

