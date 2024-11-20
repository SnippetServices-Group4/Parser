package com.services.group4.parser.dto.result;

import com.services.group4.parser.common.TestState;

public record TestResponseDto(Long snippetId, String testId, TestState testState) {
}
