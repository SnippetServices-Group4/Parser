package com.services.group4.parser.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FormatRulesDto {
  private boolean spaceBeforeColon;
  private boolean spaceAfterColon;
  private boolean equalSpaces;
  private Integer printLineBreaks;
  private Integer indentSize;

  public FormatRulesDto() {}
}
