package com.services.group4.parser.services;

import com.services.group4.parser.common.Language;
import com.services.group4.parser.dto.ExecuteRequestDto;
import com.services.group4.parser.dto.ExecuteResultDto;
import input.InputHandler;
import input.InputQueue;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import output.OutputResult;
import output.OutputString;
import runner.Runner;

@Service
public class ParserService {
  List<Language> languages;
  SnippetService snippetService;

  @Autowired
  public ParserService(SnippetService snippetService) {
    List<String> versions = List.of("1.0", "1.1");
    this.languages = List.of(new Language("printscript", versions));
    this.snippetService = snippetService;
  }

  public Optional<ExecuteResultDto> execute(Long snippetId, ExecuteRequestDto request) {
    String language = request.getLanguage();
    String version = request.getVersion();

    if (languages.stream()
        .noneMatch(l -> l.getLangName().equals(language) && l.getVersion().contains(version))) {
      throw new NoSuchElementException("Language not supported");
    }

    Optional<String> snippet = snippetService.getSnippet(snippetId);

    ExecuteResultDto resultDto = getExecuteResultDto(snippet, version);

    return Optional.of(resultDto);
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
}
