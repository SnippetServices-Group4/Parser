package com.services.group4.parser.services.async;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.group4.parser.dto.request.FormatRulesDto;
import java.time.Duration;
import java.util.Map;

import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.services.ParserService;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

@Component
public class FormatEventConsumer extends RedisStreamConsumer<String> {
  private final ObjectMapper mapper;
  private final ParserService parserService;

  @Autowired
  public FormatEventConsumer(
      @Value("${stream.final.format.key}") String streamKey,
      @Value("${groups.format}") String groupId,
      @NotNull RedisTemplate<String, String> redis,
      @NotNull ParserService parserService) {
    super(streamKey, groupId, redis);
    mapper = new ObjectMapper();
    this.parserService = parserService;
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
      String configJson = (String) messageMap.get("formatRules");
      System.out.println("SnippetId: " + snippetId);
      System.out.println("Config JSON String: " + configJson);

      // Optionally parse the `config` field if needed
      Map<String, Object> configMap = mapper.readValue(configJson, new TypeReference<>() {});
      System.out.println("Parsed Config as Map: " + configMap);

      FormatRulesDto config = mapper.convertValue(configMap, FormatRulesDto.class);
      System.out.println("Parsed Config as DTO: " + config);

      FormattingRequestDto formattingRequest = new FormattingRequestDto(config, messageMap.get("language").toString(), messageMap.get("version").toString());
      System.out.println("Formatting Request: " + formattingRequest);

      //       TODO: Call ParserService to format the snippet
      parserService.format(snippetId, formattingRequest);
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
