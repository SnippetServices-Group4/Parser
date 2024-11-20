package com.services.group4.parser.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
@Getter
@Setter
public class TestRequestDto extends ProcessingRequestDto {
  @NotNull(message = "The testId is required")
  private String testId;

  private List<String> inputs;

  private List<String> outputs;

  @NotNull(message = "Snippet id required")
  private Long snippetId;
}
