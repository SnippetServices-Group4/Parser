package com.services.group4.parser.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
@Getter
@Setter
public class ProcessingRequestDto {
  @NotNull(message = "The version is required")
  private String version;

  @NotNull(message = "The language is required")
  private String language;
}
