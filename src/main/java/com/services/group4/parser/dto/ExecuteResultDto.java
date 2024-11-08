package com.services.group4.parser.dto;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class ExecuteResultDto {
    private String printLog;
    private String errorLog;

    public ExecuteResultDto(String printLog, String errorLog) {
        this.printLog = printLog;
        this.errorLog = errorLog;
    }

}
