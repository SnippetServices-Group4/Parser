package com.services.group4.parser.dto.result;

import jakarta.validation.constraints.NotNull;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
@Getter
@Setter
public class FormattingResultDto {
    @NotNull(message = "The formatted code is required")
    private String formattedCode;

    @NotNull(message = "The language is required")
    private String language;

    @NotNull(message = "The version is required")
    private String version;

    //TODO: this should not be returned
    @NotNull(message = "The rules used are required")
    private String rulesUsed;

    public FormattingResultDto() {}

    public FormattingResultDto(String formattedCode, String language, String version, String rulesUsed) {
        this.formattedCode = formattedCode;
        this.language = language;
        this.version = version;
        this.rulesUsed = rulesUsed;
    }
}
