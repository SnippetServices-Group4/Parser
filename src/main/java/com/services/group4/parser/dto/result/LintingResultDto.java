package com.services.group4.parser.dto.result;

import jakarta.validation.constraints.NotNull;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import report.Report;
import java.util.List;

@Generated
@Getter
@Setter
public class LintingResultDto {
  @NotNull(message = "The snippet id is required")
  private Long snippetId;

  @NotNull(message = "The report is required")
  private List<Report> report;

  @NotNull(message = "The language is required")
  private String language;

  @NotNull(message = "The version is required")
  private String version;

  // TODO: this should not be returned
  @NotNull(message = "The rules used are required")
  private String rulesUsed;

  public LintingResultDto() {}

    public LintingResultDto(Long snippetId, List<Report> report, String language, String version, String rulesUsed) {
        this.snippetId = snippetId;
        this.report = report;
        this.language = language;
        this.version = version;
        this.rulesUsed = rulesUsed;
    }
}
