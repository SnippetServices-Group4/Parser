package com.services.group4.parser.services;

import com.services.group4.parser.clients.BucketClient;
import java.util.Optional;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Generated
@Service
public class BlobStorageService {
  private final BucketClient bucketClient;

  @Autowired
  public BlobStorageService(BucketClient bucketClient) {
    this.bucketClient = bucketClient;
  }

  public Optional<String> getSnippet(String container, Long id) {
    ResponseEntity<String> response = bucketClient.getSnippet(container, id);
    if (response.hasBody()) {
      log.info("Snippet with id: {} found in bucket", id);
      return Optional.ofNullable(response.getBody());
    }

    log.info("Snippet with id: {} not found in bucket", id);
    return Optional.empty();
  }
}
