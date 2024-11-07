package com.services.group4.parser.service;

import com.services.group4.parser.clients.BucketClient;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
      return Optional.ofNullable(response.getBody());
    }
    return Optional.empty();
  }
}
