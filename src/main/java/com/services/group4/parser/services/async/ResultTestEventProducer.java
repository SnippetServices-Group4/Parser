package com.services.group4.parser.services.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.group4.parser.common.TestState;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResultTestEventProducer {
  private final String streamKey;
  private final RedisTemplate<String, String> redis;
  private final ObjectMapper mapper;

  @Autowired
  public ResultTestEventProducer(
      @Value("${stream.result.test.key}") String streamKey,
      @NotNull RedisTemplate<String, String> redis,
      ObjectMapper mapper) {
    this.streamKey = streamKey;
    this.redis = redis;
    this.mapper = mapper;
  }

  public void emit(String jsonMessage) {
    ObjectRecord<String, String> result =
        StreamRecords.newRecord().ofObject(jsonMessage).withStreamKey(streamKey);

    redis.opsForStream().add(result);
  }

  public void publishEvent(Long testCaseId, TestState testState) {
    System.out.println("\nRESULT TEST EVENT PRODUCER\n\n");
    System.out.println("Test case ID: " + testCaseId);

    try {
      String message =
          mapper.writeValueAsString(
              Map.of(
                  "testCaseId", testCaseId,
                  "status", testState));

      emit(message);
    } catch (Exception e) {
      System.err.println("Error serializing message: " + e.getMessage());
    }
  }
}
