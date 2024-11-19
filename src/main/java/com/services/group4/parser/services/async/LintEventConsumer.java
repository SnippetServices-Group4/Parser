package com.services.group4.parser.services.async;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.group4.parser.dto.request.LintRulesDto;
import java.time.Duration;
import java.util.Map;

import com.services.group4.parser.dto.request.LintingRequestDto;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

@Component
public class LintEventConsumer extends RedisStreamConsumer<String> {
  private final ObjectMapper mapper;

  @Autowired
  public LintEventConsumer(
      @Value("${stream.lint.key}") String streamKey,
      @Value("${groups.lint}") String groupId,
      @NotNull RedisTemplate<String, String> redis) {
    super(streamKey, groupId, redis);
    mapper = new ObjectMapper();
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

      LintingRequestDto lintingRequest = new LintingRequestDto(config, messageMap.get("language").toString(), messageMap.get("version").toString());
      System.out.println("Linting Request: " + lintingRequest);

      //      TODO: Call ParserService to lint the snippet
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
