package com.services.group4.parser.consumer.lint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class LintStreamConsumer extends RedisStreamConsumer<RulesMessageProduct> {
  @Autowired
  public LintStreamConsumer(
      @Value("${stream.lint.key}") String streamKey,
      @Value("${groups.lint}") String groupId,
      @NotNull RedisTemplate<String, String> redis) {
    super(streamKey, groupId, redis);
  }

  @Override
  protected void onMessage(@NotNull ObjectRecord<String, RulesMessageProduct> objectRecord) {
    RulesMessageProduct product = objectRecord.getValue();
    System.out.println("UserId: " + product.userId());

    ObjectMapper mapper = new ObjectMapper();
    try {
      Map<String, Object> payloadMap =
          mapper.readValue(product.jsonPayload(), new TypeReference<>() {});
      System.out.println("JSON Payload: " + payloadMap);
    } catch (Exception e) {
      System.err.println("Error deserializing jsonPayload: " + e.getMessage());
    }
  }

  @NotNull
  @Override
  protected StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, RulesMessageProduct>>
      options() {
    return StreamReceiver.StreamReceiverOptions.builder()
        .pollTimeout(Duration.ofSeconds(1))
        .targetType(RulesMessageProduct.class)
        .build();
  }
}
