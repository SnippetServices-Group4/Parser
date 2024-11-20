package com.services.group4.parser.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.group4.parser.DotenvConfig;
import com.services.group4.parser.common.ValidationState;
import com.services.group4.parser.common.response.DataTuple;
import com.services.group4.parser.dto.request.FormatRulesDto;
import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.dto.request.ProcessingRequestDto;
import com.services.group4.parser.dto.result.ExecuteResultDto;
import com.services.group4.parser.dto.result.FormattingResultDto;
import com.services.group4.parser.dto.result.LintingResultDto;
import com.services.group4.parser.dto.result.ResponseDto;
import com.services.group4.parser.services.ParserService;
import com.services.group4.parser.services.SnippetService;
import com.services.group4.parser.utils.TestDtoProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ParserController.class)
public class ParserControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockBean private ParserService parserService;

  @MockBean private SnippetService snippetService;

  @Autowired private ObjectMapper objectMapper;

  private static final String BASE_URL = "/parsers";

  @BeforeAll
  public static void setupEnv() {
    DotenvConfig.loadEnv();
  }

  @Test
  void shouldExecuteSnippet() throws Exception {
    ProcessingRequestDto request = new ProcessingRequestDto("printscript", "1.1", "");
    String requestJson = objectMapper.writeValueAsString(request);

    when(parserService.execute(1L, request))
        .thenReturn(
            new ResponseEntity<>(
                new ResponseDto<>("", new DataTuple<>("", new ExecuteResultDto())), HttpStatus.OK));
    mockMvc
        .perform(
            post(BASE_URL + "/execute/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isOk());
  }

  @Test
  void shouldFormatSnippet() throws Exception {
    FormatRulesDto formatRulesDto = TestDtoProvider.getFormatRulesDto();
    FormattingRequestDto request = new FormattingRequestDto(formatRulesDto, "printscript", "1.1");
    String requestJson = objectMapper.writeValueAsString(request);

    when(parserService.format(1L, request))
        .thenReturn(
            new ResponseEntity<>(
                new ResponseDto<>("", new DataTuple<>("", new FormattingResultDto())),
                HttpStatus.OK));

    mockMvc
        .perform(
            post(BASE_URL + "/format/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isOk());
  }

  @Test
  void shouldLintSnippet() throws Exception {
    when(parserService.lint(1L, TestDtoProvider.getAnalyzeCamelCaseRequestDto()))
        .thenReturn(
            new ResponseEntity<>(
                new ResponseDto<>("", new DataTuple<>("", new LintingResultDto())), HttpStatus.OK));
    mockMvc
        .perform(
            post(BASE_URL + "/lint/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        TestDtoProvider.getAnalyzeCamelCaseRequestDto())))
        .andExpect(status().isOk());
  }

  @Test
  void shouldValidateSnippet() throws Exception {
    ProcessingRequestDto request = new ProcessingRequestDto("printscript", "1.1", "content");
    when(parserService.validate(request))
        .thenReturn(
            new ResponseEntity<>(
                new ResponseDto<>("a", new DataTuple<>("a", ValidationState.VALID)),
                HttpStatus.OK));
    mockMvc
        .perform(
            post(BASE_URL + "/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldRunTest() throws Exception {
    when(parserService.runTest(TestDtoProvider.getTestRequestDto()))
        .thenReturn(
            new ResponseEntity<>(new ResponseDto<>("", new DataTuple<>("", null)), HttpStatus.OK));
    mockMvc
        .perform(
            post(BASE_URL + "/runTest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDtoProvider.getTestRequestDto())))
        .andExpect(status().isOk());
  }
}
