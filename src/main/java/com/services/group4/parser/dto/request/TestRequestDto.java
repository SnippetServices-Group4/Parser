package com.services.group4.parser.dto.request;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Generated
public record TestRequestDto (
    @NotNull(message = "The testId is required") String testId,
    List<String> inputs,
    List<String> outputs,
    String version,
    String language,
    @NotNull(message = "Snippet id required") Long snippetId
) {}
