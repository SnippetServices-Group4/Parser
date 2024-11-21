package com.services.group4.parser.services.adapter;

import com.google.gson.JsonObject;
import com.services.group4.parser.dto.request.FormatRulesDto;

public class FormatConfigAdapter {
  public String adaptFormatConfig(FormatRulesDto config) {
    JsonObject result = new JsonObject();

    JsonObject colonRules = getColonRules(config);

    result.add("colonRules", colonRules);

    result.addProperty("equalSpaces", config.isEqualSpaces());
    result.addProperty("printLineBreaks", config.getPrintLineBreaks());
    result.addProperty("indentSize", config.getIndentSize());

    return result.toString();
  }

  private JsonObject getColonRules(FormatRulesDto config) {
    JsonObject colonRules = new JsonObject();

    colonRules.addProperty("before", config.isSpaceBeforeColon());
    colonRules.addProperty("after", config.isSpaceAfterColon());

    return colonRules;
  }
}
