package com.services.group4.parser.services.async;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.group4.parser.dto.request.LintRulesDto;
import com.services.group4.parser.dto.request.LintingRequestDto;
import com.services.group4.parser.dto.result.LintingResultDto;
import com.services.group4.parser.dto.result.ResponseDto;
import com.services.group4.parser.services.ParserService;
import com.services.group4.parser.services.utils.LintStatus;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import report.Report;

@Component
public class FinalLintEventConsumer extends RedisStreamConsumer<String> {
  private final ObjectMapper mapper;
  private final ParserService parserService;
  private final ResultLintEventProducer publisher;

  @Autowired
  public FinalLintEventConsumer(
      @Value("${stream.final.lint.key}") String streamKey,
      @Value("${groups.lint}") String groupId,
      @NotNull RedisTemplate<String, String> redis,
      @NotNull ParserService parserService,
      @NotNull ResultLintEventProducer publisher) {
    super(streamKey, groupId, redis);
    mapper = new ObjectMapper();
    this.parserService = parserService;
    this.publisher = publisher;
  }

  @Override
  protected void onMessage(@NotNull ObjectRecord<String, String> objectRecord) {
    String jsonString = objectRecord.getValue();
    System.out.println("Received JSON: " + jsonString);

    try {
      // Deserialize the JSON string into a Map
      Map<String, Object> messageMap = mapper.readValue(jsonString, new TypeReference<>() {});
      System.out.println("Parsed JSON as Map: " + messageMap);

      // Access specific fields from the Map
      Long snippetId = (Long) ((Integer) messageMap.get("snippetId")).longValue();
      String configJson = (String) messageMap.get("lintRules");
      System.out.println("SnippetId: " + snippetId);
      System.out.println("Config JSON String: " + configJson);

      // Optionally parse the `config` field if needed
      Map<String, Object> configMap = mapper.readValue(configJson, new TypeReference<>() {});
      System.out.println("Parsed Config as Map: " + configMap);

      LintRulesDto config = mapper.convertValue(configMap, LintRulesDto.class);
      System.out.println("Parsed Config as DTO: " + config);

      LintingRequestDto lintingRequest =
          new LintingRequestDto(
              config, messageMap.get("language").toString(), messageMap.get("version").toString());
      System.out.println("Linting Request: " + lintingRequest);

      //      TODO: Call ParserService to lint the snippet
      ResponseEntity<ResponseDto<LintingResultDto>> result =
          parserService.lint(snippetId, lintingRequest);
      HttpStatusCode status = result.getStatusCode();

      if (status == HttpStatus.OK) {
        List<Report> reports = Objects.requireNonNull(result.getBody()).data().data().getReport();

        LintStatus lintStatus = reports.isEmpty() ? LintStatus.COMPLIANT : LintStatus.NON_COMPLIANT;

        publisher.publishEvent(snippetId, lintStatus, reports);
      } else {
        System.out.println("Linting failed");
      }

    } catch (Exception e) {
      System.err.println("Error deserializing message: " + e.getMessage());
    }
  }

  @Override
  protected @NotNull StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>>
      options() {
    return StreamReceiver.StreamReceiverOptions.builder()
        .pollTimeout(Duration.ofSeconds(1))
        .targetType(String.class)
        .build();
  }
}
