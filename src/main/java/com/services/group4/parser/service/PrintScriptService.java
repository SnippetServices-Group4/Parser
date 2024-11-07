package com.services.group4.parser.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Service;
import output.OutputListString;
import output.OutputReport;
import runner.Runner;

@Service
public class PrintScriptService {
  private final Runner runner = new Runner();

  public void lintSnippet(String snippetContent, String version, String config) {
    InputStream inputStream = new ByteArrayInputStream(snippetContent.getBytes());
    OutputReport output = new OutputReport();
    runner.analyze(inputStream, version, config, output);
  }

  public void formatSnippet(String snippetContent, String version, String config) {
    InputStream inputStream = new ByteArrayInputStream(snippetContent.getBytes());
    OutputListString output = new OutputListString();
    try {
      runner.format(inputStream, version, config, output);
    } catch (IOException e) {
      System.out.println("Error while formatting the snippet: " + e.getMessage());
    }
  }
}
