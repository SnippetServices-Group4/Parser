package com.services.group4.parser.dto.result;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class ExecuteResultDto {
  @NotNull(message = "The snippet id is required")
  private Long snippetId;

  @NotNull(message = "The print log is required")
  private String printLog;

  @NotNull(message = "The error log is required")
  private String errorLog;

  public ExecuteResultDto(Long snippetId, String printLog, String errorLog) {
    this.snippetId = snippetId;
    this.printLog = printLog;
    this.errorLog = errorLog;
  }

  // This constructor is required for the test
  public ExecuteResultDto() {}
}
