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
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import report.Report;

@Component
public class ResultLintEventProducer {
  private final String streamKey;
  private final ReactiveRedisTemplate<String, String> redis;
  private final ObjectMapper mapper;

  @Autowired
  public ResultLintEventProducer(
      @Value("${stream.result.lint.key}") String streamKey,
      @NotNull ReactiveRedisTemplate<String, String> redis,
      ObjectMapper mapper) {
    this.streamKey = streamKey;
    this.redis = redis;
    this.mapper = mapper;
  }

  public Mono<ObjectRecord<String, String>> emit(String jsonMessage) {
    ObjectRecord<String, String> result =
        StreamRecords.newRecord().ofObject(jsonMessage).withStreamKey(streamKey);

    return redis.opsForStream().add(result).thenReturn(result);
  }

  public void publishEvent(Long snippetId, LintStatus status, List<Report> reports) {
    System.out.println("\nRESULT LINT EVENT PRODUCER\n\n");

    try {
      String reportsJson = mapper.writeValueAsString(reports);
      String message =
          mapper.writeValueAsString(
              Map.of(
                  "snippetId", snippetId,
                  "status", status,
                  "reports", reportsJson));

      emit(message).block();
    } catch (Exception e) {
      System.err.println("Error serializing message: " + e.getMessage());
    }
  }
}
