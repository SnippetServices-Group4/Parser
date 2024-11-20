package com.services.group4.parser.utils;

import com.services.group4.parser.dto.request.FormatRulesDto;
import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.dto.request.LintRulesDto;
import com.services.group4.parser.dto.request.LintingRequestDto;
import com.services.group4.parser.dto.request.ProcessingRequestDto;
import com.services.group4.parser.dto.request.TestRequestDto;
import java.util.List;

public class TestDtoProvider {

  public static ProcessingRequestDto getProcessingRequestDto() {
    String language = "printscript";
    String version = "1.1";

    return new ProcessingRequestDto(language, version);
  }

  public static LintingRequestDto getAnalyzeCamelCaseRequestDto() {
    LintRulesDto rules = getLintCamelCaseRulesDto();

    String language = "printscript";
    String version = "1.1";

    return new LintingRequestDto(rules, language, version);
  }

  public static LintingRequestDto getAnalyzeSnakeCaseRequestDto() {
    LintRulesDto rules = new LintRulesDto();
    rules.setPrintLnAcceptsExpressions(true);
    rules.setWritingConventionName("snakeCase");
    rules.setReadInputAcceptsExpressions(true);

    String language = "printscript";
    String version = "1.1";

    return new LintingRequestDto(rules, language, version);
  }

  public static LintRulesDto getLintCamelCaseRulesDto() {
    LintRulesDto rules = new LintRulesDto();
    rules.setPrintLnAcceptsExpressions(true);
    rules.setWritingConventionName("camelCase");
    rules.setReadInputAcceptsExpressions(true);

    return rules;
  }

  public static FormattingRequestDto getFormattingRequestDto() {
    FormatRulesDto rules = getFormatRulesDto();

    String language = "printscript";
    String version = "1.1";

    FormattingRequestDto request = new FormattingRequestDto();
    request.setLanguage(language);
    request.setVersion(version);
    request.setFormatRules(rules);

    return request;
  }

  public static FormatRulesDto getFormatRulesDto() {
    FormatRulesDto rules = new FormatRulesDto();
    rules.setEqualSpaces(true);
    rules.setPrintLineBreaks(4);
    rules.setIndentSize(4);
    rules.setSpaceBeforeColon(true);
    rules.setSpaceAfterColon(true);

    return rules;
  }

  public static TestRequestDto getTestRequestDto() {
    List<String> input = List.of("1");
    List<String> output = List.of("1");

    String language = "printscript";
    String version = "1.1";

    TestRequestDto request = new TestRequestDto();
    request.setLanguage(language);
    request.setVersion(version);
    request.setInputs(input);
    request.setOutputs(output);

    return request;
  }
}
