package com.services.group4.parser.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Generated;
import lombok.Getter;

@Generated
@Getter
public class LintingRequestDto {
  @NotNull(message = "The version is required")
  private LintRulesDto lintRules;

  private String language;
  private String version;

  public LintingRequestDto() {}

  public LintingRequestDto(LintRulesDto lintRules, String language, String version) {
    this.lintRules = lintRules;
    this.language = language;
    this.version = version;
  }
}
