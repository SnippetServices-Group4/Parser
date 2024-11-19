package com.services.group4.parser.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnippetService {
  private final BlobStorageService blobStorageService;
  private final String container = "snippets";

  @Autowired
  public SnippetService(BlobStorageService blobStorageService) {
    this.blobStorageService = blobStorageService;
  }

  public String getSnippet(Long snippetId) {
    Optional<String> content = blobStorageService.getSnippet(container, snippetId);

    if (content.isEmpty()) {
      throw new NoSuchElementException("Snippet content not found");
    }

    return content.get();
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
