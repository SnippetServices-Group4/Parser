package com.services.group4.parser.dto;

import com.services.group4.parser.common.ValidationState;
import jakarta.validation.constraints.NotNull;
import lombok.Generated;
import lombok.Getter;

@Generated
@Getter
public class ValidateResultDto {
    @NotNull
    private String message;

    @NotNull
    private ValidationState state;

    @NotNull
    private String version;

    @NotNull
    private String language;

    public ValidateResultDto() {}

    public ValidateResultDto(String message, ValidationState state, String version, String language) {
        this.message = message;
        this.state = state;
        this.version = version;
        this.language = language;
    }

}
