package com.services.group4.parser.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SnippetService {
  private final BlobStorageService blobStorageService;
  private final String container = "snippets";

  @Autowired
  public SnippetService(BlobStorageService blobStorageService) {
      this.blobStorageService = blobStorageService;
  }

  public Optional<String> getSnippet(Long snippetId) {
    Optional<String> content = blobStorageService.getSnippet(container, snippetId);

    if (content.isEmpty()) {
      throw new NoSuchElementException("Snippet content not found");
    }

    return content;
  }

  public void setEnv() {
    String snippet1 = "let x : Number = 5;";
    String snippet2 = "let y:Number = 10 + 10;";
    String snippet3 = "println('Hello World!');";
    List<String> snippets = List.of(snippet1, snippet2, snippet3);

    Long i = 0L;
    for (String snippet : snippets) {
      i++;
      blobStorageService.saveSnippet(container, i, snippet);
    }
  }

}
