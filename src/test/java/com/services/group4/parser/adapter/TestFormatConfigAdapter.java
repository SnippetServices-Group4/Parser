package com.services.group4.parser.adapter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.services.group4.parser.dto.request.FormatRulesDto;
import com.services.group4.parser.services.adapter.FormatConfigAdapter;
import org.junit.jupiter.api.Test;

public class TestFormatConfigAdapter {
  private final FormatConfigAdapter formatConfigAdapter = new FormatConfigAdapter();

  private FormatRulesDto getFormatRulesDto() {
    FormatRulesDto formatRulesDto = new FormatRulesDto();

    formatRulesDto.setSpaceBeforeColon(true);
    formatRulesDto.setSpaceAfterColon(true);
    formatRulesDto.setEqualSpaces(true);
    formatRulesDto.setPrintLineBreaks(1);
    formatRulesDto.setIndentSize(1);

    return formatRulesDto;
  }

  @Test
  public void testAdapting() {
    FormatRulesDto lintRulesDto = getFormatRulesDto();

    String adaptedLintRules = formatConfigAdapter.adaptFormatConfig(lintRulesDto);

    assertTrue(adaptedLintRules.contains("colonRules"));
    assertTrue(adaptedLintRules.contains("equalSpaces"));
    assertTrue(adaptedLintRules.contains("printLineBreaks"));
    assertTrue(adaptedLintRules.contains("indentSize"));
  }
}
