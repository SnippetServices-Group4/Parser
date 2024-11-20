package com.services.group4.parser.services.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.group4.parser.services.utils.LintStatus;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import report.Report;

@Component
public class ResultLintEventProducer {
  private final String streamKey;
  private final RedisTemplate<String, String> redis;
  private final ObjectMapper mapper;

  @Autowired
  public ResultLintEventProducer(
      @Value("${stream.result.lint.key}") String streamKey,
      @NotNull RedisTemplate<String, String> redis,
      ObjectMapper mapper) {
    this.streamKey = streamKey;
    this.redis = redis;
    this.mapper = mapper;
  }

  public void emit(String jsonMessage) {
    ObjectRecord<String, String> result =
        StreamRecords.newRecord().ofObject(jsonMessage).withStreamKey(streamKey);
    System.out.println("MESSAGE PUBLISHED");
    System.out.println();
    System.out.println();
    redis.opsForStream().add(result);
  }

  public void publishEvent(Long snippetId, LintStatus status, List<Report> reports) {
    try {
      String reportsJson = mapper.writeValueAsString(reports);
      String message =
          mapper.writeValueAsString(
              Map.of(
                  "snippetId", snippetId,
                  "status", status,
                  "reports", reportsJson));

      System.out.println("Publishing lint result event: " + message);

      emit(message);
    } catch (Exception e) {
      System.err.println("Error serializing message: " + e.getMessage());
    }
  }
}
