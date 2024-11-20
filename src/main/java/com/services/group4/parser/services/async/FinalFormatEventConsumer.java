package com.services.group4.parser.services.async;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.group4.parser.dto.request.FormatRulesDto;
import com.services.group4.parser.dto.request.FormattingRequestDto;
import com.services.group4.parser.services.ParserService;
import java.time.Duration;
import java.util.Map;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

@Component
public class FinalFormatEventConsumer extends RedisStreamConsumer<String> {
  private final ObjectMapper mapper;
  private final ParserService parserService;

  @Autowired
  public FinalFormatEventConsumer(
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
    System.out.println("\nFINAL FORMAT EVENT CONSUMER\n\n");
    String jsonString = objectRecord.getValue();

    try {
      // Deserialize the JSON string into a Map
      Map<String, Object> messageMap = mapper.readValue(jsonString, new TypeReference<>() {});

      // Access specific fields from the Map
      Long snippetId = (Long) ((Integer) messageMap.get("snippetId")).longValue();
      String configJson = (String) messageMap.get("formatRules");

      // Optionally parse the `config` field if needed
      Map<String, Object> configMap = mapper.readValue(configJson, new TypeReference<>() {});

      FormatRulesDto config = mapper.convertValue(configMap, FormatRulesDto.class);

      FormattingRequestDto formattingRequest =
          new FormattingRequestDto(
              config, messageMap.get("language").toString(), messageMap.get("version").toString());

      parserService.format(snippetId, formattingRequest);
    } catch (Exception e) {
      System.err.println("Error deserializing message: " + e.getMessage());
    }
  }

  @Override
  protected @NotNull StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>>
      options() {
    return StreamReceiver.StreamReceiverOptions.builder()
        .pollTimeout(Duration.ofSeconds(2))
        .targetType(String.class)
        .build();
  }
}
