package com.services.group4.parser.consumer.lint;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestLintStreamProducer {
  private final String streamKey;
  private final RedisTemplate<String, String> redis;

  @Autowired
  public TestLintStreamProducer(
      @Value("${stream.lint.key}") String streamKey, @NotNull RedisTemplate<String, String> redis) {
    this.streamKey = streamKey;
    this.redis = redis;
  }

  public void emit(LintMessageProduct product) {
    ObjectRecord<String, LintMessageProduct> result =
        StreamRecords.newRecord().ofObject(product).withStreamKey(streamKey);

    redis.opsForStream().add(result);
  }

  public void publishEvent(Long userId, Map<String, Object> jsonPayload) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String jsonPayloadString = mapper.writeValueAsString(jsonPayload);
      LintMessageProduct product = new LintMessageProduct(userId, jsonPayloadString);
      emit(product);
    } catch (Exception e) {
      System.err.println("Error serializing jsonPayload: " + e.getMessage());
    }
  }
}
