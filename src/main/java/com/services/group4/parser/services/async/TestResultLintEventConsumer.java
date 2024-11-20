package com.services.group4.parser.services.async;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.group4.parser.services.utils.LintStatus;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
public class TestResultLintEventConsumer extends RedisStreamConsumer<String> {
  private final ObjectMapper mapper;

  @Autowired
  public TestResultLintEventConsumer(
      @Value("${stream.result.lint.key}") String streamKey,
      @Value("test-lint-result-group") String groupId,
      @NotNull RedisTemplate<String, String> redis,
      @NotNull ObjectMapper mapper) {
    super(streamKey, groupId, redis);
    this.mapper = mapper;
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
      LintStatus status = LintStatus.valueOf(messageMap.get("status").toString());

      System.out.println("SnippetId: " + snippetId);
      System.out.println("Status: " + status);
    } catch (Exception e) {
      System.err.println("Error deserializing message: " + e.getMessage());
    }
  }

  @Override
  protected @NotNull StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> options() {
    return StreamReceiver.StreamReceiverOptions.builder()
        .pollTimeout(Duration.ofSeconds(1))
        .targetType(String.class)
        .build();
  }
}
