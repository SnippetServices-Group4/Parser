package com.services.group4.parser.consumer.lint;

import java.time.Duration;
import org.austral.ingsis.redis.RedisStreamConsumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

@Component
public class LintStreamConsumer extends RedisStreamConsumer<LintMessageProduct> {
  @Autowired
  public LintStreamConsumer(
      @Value("${stream.lint.key}") String streamKey,
      @Value("${groups.lint}") String groupId,
      @NotNull RedisTemplate<String, String> redis) {
    super(streamKey, groupId, redis);
  }

  @Override
  protected void onMessage(@NotNull ObjectRecord<String, LintMessageProduct> objectRecord) {
    System.out.println(objectRecord.getValue().message());
  }

  @NotNull
  @Override
  protected StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, LintMessageProduct>>
      options() {
    return StreamReceiver.StreamReceiverOptions.builder()
        .pollTimeout(Duration.ofSeconds(1))
        .targetType(LintMessageProduct.class)
        .build();
  }
}
