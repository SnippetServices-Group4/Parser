package com.services.group4.parser.utils;

import com.services.group4.parser.dto.request.*;

public class TestDtoProvider {

    public static ProcessingRequestDto getProcessingRequestDto() {
        String language = "printscript";
        String version = "1.1";
        // TODO: agregar content
        return new ProcessingRequestDto(language, version, "let x : number = 5;");
    }

    public static LintingRequestDto getAnalyzeCamelCaseRequestDto() {
        String language = "printscript";
        String version = "1.1";

        LintRulesDto rules = new LintRulesDto();
        rules.setPrintLnAcceptsExpressions(true);
        rules.setWritingConventionName("camelCase");
        rules.setReadInputAcceptsExpressions(true);

        return new LintingRequestDto(rules, language, version);
    }

    public static LintingRequestDto getAnalyzeSnakeCaseRequestDto() {
        String language = "printscript";
        String version = "1.1";

        LintRulesDto rules = new LintRulesDto();
        rules.setPrintLnAcceptsExpressions(true);
        rules.setWritingConventionName("snakeCase");
        rules.setReadInputAcceptsExpressions(true);

        return new LintingRequestDto(rules, language, version);
    }

    public static FormattingRequestDto getFormattingRequestDto() {
        String language = "printscript";
        String version = "1.1";

        FormatRulesDto rules = new FormatRulesDto();
        rules.setEqualSpaces(true);
        rules.setPrintLineBreaks(4);
        rules.setIndentSize(4);
        rules.setSpaceBeforeColon(true);
        rules.setSpaceAfterColon(true);

        FormattingRequestDto request = new FormattingRequestDto();
        request.setLanguage(language);
        request.setVersion(version);
        request.setFormatRules(rules);

        return request;
    }


}
