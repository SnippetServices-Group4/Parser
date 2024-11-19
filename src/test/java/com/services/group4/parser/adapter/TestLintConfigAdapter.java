package com.services.group4.parser.adapter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.services.group4.parser.dto.LintRulesDto;
import com.services.group4.parser.services.adapter.LintConfigAdapter;
import org.junit.jupiter.api.Test;

public class TestLintConfigAdapter {
  private final LintConfigAdapter lintConfigAdapter = new LintConfigAdapter();

  private LintRulesDto getLintRulesDto() {
    LintRulesDto lintRulesDto = new LintRulesDto();

    lintRulesDto.setWritingConventionName("camelCase");
    lintRulesDto.setPrintLnAcceptsExpressions(true);
    lintRulesDto.setReadInputAcceptsExpressions(true);

    return lintRulesDto;
  }

  @Test
  public void testAdapting() {
    LintRulesDto lintRulesDto = getLintRulesDto();

    String adaptedLintRules = lintConfigAdapter.adaptLintConfig(lintRulesDto);

    assertTrue(adaptedLintRules.contains("writingConvention"));
    assertTrue(adaptedLintRules.contains("callExpression"));
  }

  private LintRulesDto getLintRulesDtoNoConvention() {
    LintRulesDto lintRulesDto = new LintRulesDto();

    lintRulesDto.setPrintLnAcceptsExpressions(true);
    lintRulesDto.setReadInputAcceptsExpressions(true);

    return lintRulesDto;
  }

  @Test
  public void testAdaptingNoConvention() {
    LintRulesDto lintRulesDto = getLintRulesDtoNoConvention();

    String adaptedLintRules = lintConfigAdapter.adaptLintConfig(lintRulesDto);

    assertTrue(adaptedLintRules.contains("callExpression"));
    assertFalse(adaptedLintRules.contains("identifier"));
    assertFalse(adaptedLintRules.contains("writingConvention"));
  }
}
