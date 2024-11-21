package com.services.group4.parser.services;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SnippetService {
  private final BlobStorageService blobStorageService;
  private final String container = "snippets";

  @Autowired
  public SnippetService(BlobStorageService blobStorageService) {
    this.blobStorageService = blobStorageService;
  }

  public Optional<String> getSnippet(Long snippetId) {
    log.info("Getting snippet with id: {} from bucket", snippetId);
    return blobStorageService.getSnippet(container, snippetId);
  }
}
