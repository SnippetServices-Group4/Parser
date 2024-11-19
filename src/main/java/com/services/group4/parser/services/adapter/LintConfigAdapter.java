package com.services.group4.parser.services.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.services.group4.parser.dto.request.LintRulesDto;

public class LintConfigAdapter {
  public String adaptLintConfig(LintRulesDto config) {
    JsonObject result = new JsonObject();

    JsonObject identifier = getIdentifierConfig(config);
    JsonObject callExpression = getCallExpressionConfig(config);

    if (!identifier.entrySet().isEmpty()) {
      result.add("identifier", identifier);
    }
    if (!callExpression.entrySet().isEmpty()) {
      result.add("callExpression", callExpression);
    }

    return result.toString();
  }

  private JsonObject getIdentifierConfig(LintRulesDto config) {
    JsonObject identifier = new JsonObject();
    JsonObject writingConvention = new JsonObject();

    String writingConventionName = config.getWritingConventionName();

    if (writingConventionName == null) {
      return identifier;
    } else if (writingConventionName.equals("snakeCase")) {
      writingConvention.addProperty("conventionName", "snakeCase");
      writingConvention.addProperty("conventionPattern", "^[a-z]+(_[a-z0-9]+)*$");
    } else if (writingConventionName.equals("camelCase")) {
      writingConvention.addProperty("conventionName", "camelCase");
      writingConvention.addProperty("conventionPattern", "^[a-z]+(?:[A-Z]?[a-z0-9]+)*$");
    } else {
      return identifier;
    }

    identifier.add("writingConvention", writingConvention);

    return identifier;
  }

  private JsonObject getCallExpressionConfig(LintRulesDto config) {
    JsonObject callExpression = new JsonObject();
    JsonArray basicArgs = new JsonArray();
    basicArgs.add("IDENTIFIER");
    basicArgs.add("STRING_LITERAL");
    basicArgs.add("NUMBER_LITERAL");
    basicArgs.add("BOOLEAN_LITERAL");

    JsonObject printlnArguments = getArguments(config.isPrintLnAcceptsExpressions(), basicArgs);
    callExpression.add("println", printlnArguments);

    JsonObject readInputArguments = getArguments(config.isReadInputAcceptsExpressions(), basicArgs);
    ;
    callExpression.add("readInput", readInputArguments);

    return callExpression;
  }

  private JsonObject getArguments(boolean isStrict, JsonArray basicArgs) {
    JsonObject args = new JsonObject();
    JsonArray copyBasicArgs = basicArgs.deepCopy();

    if (!isStrict) {
      copyBasicArgs.add("CALL_EXPRESSION");
      copyBasicArgs.add("BINARY_EXPRESSION");
    }
    args.add("arguments", copyBasicArgs);

    return args;
  }
}
