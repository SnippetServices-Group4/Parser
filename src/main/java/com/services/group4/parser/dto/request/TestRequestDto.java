package com.services.group4.parser.dto.request;

import java.util.List;
import lombok.Generated;

@Generated
public record TestRequestDto(
    List<String> inputs, List<String> outputs, String version, String language) {}
