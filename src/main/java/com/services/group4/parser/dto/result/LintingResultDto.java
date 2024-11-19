package com.services.group4.parser.dto.result;

import jakarta.validation.constraints.NotNull;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import output.OutputReport;

@Generated
@Getter
@Setter
public class LintingResultDto {

    @NotNull(message = "The report is required")
    private OutputReport report;

    @NotNull(message = "The language is required")
    private String language;

    @NotNull(message = "The version is required")
    private String version;

    //TODO: this should not be returned
    @NotNull(message = "The rules used are required")
    private String rulesUsed;

    public LintingResultDto() {}

    public LintingResultDto(OutputReport  report, String language, String version, String rulesUsed) {
        this.report = report;
        this.language = language;
        this.version = version;
        this.rulesUsed = rulesUsed;
    }
}
