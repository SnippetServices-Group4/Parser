package com.services.group4.parser.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Generated;

@Generated
public record TestRequestDto(
    @NotNull(message = "The testId is required") String testId,
    List<String> inputs,
    List<String> outputs,
    String version,
    String language) {}
