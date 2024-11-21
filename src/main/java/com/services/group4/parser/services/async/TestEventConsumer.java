package com.services.group4.parser.services.async;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.group4.parser.common.TestState;
import com.services.group4.parser.dto.request.TestRequestDto;
import com.services.group4.parser.dto.result.ResponseDto;
import com.services.group4.parser.services.ParserService;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class TestEventConsumer extends RedisStreamConsumer<String> {
  private final ObjectMapper mapper;
  private final ParserService parserService;
  private final ResultTestEventProducer publisher;

  public TestEventConsumer(
      @Value("${stream.initial.test.key}") String streamKey,
      @Value("${groups.test}") String groupId,
      @NotNull RedisTemplate<String, String> redis,
      @NotNull ObjectMapper mapper,
      @NotNull ParserService parserService,
      @NotNull ResultTestEventProducer publisher) {
    super(streamKey, groupId, redis);
    this.mapper = mapper;
    this.parserService = parserService;
    this.publisher = publisher;
  }

  @Override
  protected void onMessage(@NotNull ObjectRecord<String, String> objectRecord) {
    System.out.println("\nTEST EVENT CONSUMER\n\n");

    String jsonString = objectRecord.getValue();
    try {
      Map<String, Object> messageMap = mapper.readValue(jsonString, new TypeReference<>() {});
      String language = (String) messageMap.get("language");
      Map<String, Object> languageMap = mapper.readValue(language, new TypeReference<>() {});
      Long testId = (Long) ((Integer) messageMap.get("testId")).longValue();

      System.out.println("Testing test with ID: " + testId);

      String jsonInputs = (String) messageMap.get("inputs");
      String jsonOutputs = (String) messageMap.get("outputs");

      List<String> inputs = mapper.readValue(jsonInputs, new TypeReference<>() {});
      List<String> outputs = mapper.readValue(jsonOutputs, new TypeReference<>() {});

      TestRequestDto testRequestDto = new TestRequestDto(
          inputs,
          outputs,
          (String) languageMap.get("version"),
          (String) languageMap.get("name")
      );

      Long snippetId = (Long) ((Integer) messageMap.get("snippetId")).longValue();
      ResponseEntity<ResponseDto<TestState>> result = parserService.runTest(testRequestDto, snippetId);
      HttpStatusCode status = result.getStatusCode();

      TestState testState;
      if (status == HttpStatus.OK) {
        testState = result.getBody().data().data();
        publisher.publishEvent(testId, testState);
      } else {
        testState = TestState.FAILED;
      }
      publisher.publishEvent(testId, testState);
    } catch (Exception e) {
      System.err.println("Error deserializing message: " + e.getMessage());
    }
  }

  @Override
  protected @NotNull StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> options() {
    return StreamReceiver.StreamReceiverOptions.builder()
        .pollTimeout(Duration.ofSeconds(7))
        .targetType(String.class)
        .build();
  }
}
