package com.services.group4.parser.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Generated;
import lombok.Getter;

@Generated
@Getter
public class FormattingRequestDto {
    @NotNull(message = "The version is required")
    private FormatRulesDto formatRules;
    private String language;
    private String version;

    public FormattingRequestDto() {}

    public FormattingRequestDto(FormatRulesDto formatRules, String language, String version) {
        this.formatRules = formatRules;
        this.language = language;
        this.version = version;
    }
}
