package com.services.group4.parser.services;

import java.util.List;
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

  public void setEnv() {
    String snippet1 = "let x : number = 5;";
    String snippet2 = "let y:number = 10 + 10;";
    String snippet3 = "println('Hello World!');";
    String snippet4 = "let my_var : string = 'Hello world' + 2; println(my_var);";
    String invalidSnippet = "let x : number = 5";
    List<String> snippets = List.of(snippet1, snippet2, snippet3, snippet4, invalidSnippet);

    Long i = 0L;
    for (String snippet : snippets) {
      i++;
      blobStorageService.saveSnippet(container, i, snippet);
    }
  }
}
