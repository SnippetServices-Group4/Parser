package com.services.group4.parser.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.services.group4.parser.services.BlobStorageService;
import com.services.group4.parser.services.SnippetService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SnippetServiceTest {
  @InjectMocks private SnippetService snippetService;

  @Mock private BlobStorageService blobStorageService;

  @Test
  void test() {
    when(blobStorageService.getSnippet(anyString(), anyLong()))
        .thenReturn(Optional.of("Hello World"));
    assert snippetService.getSnippet(1L).isPresent();
  }
}
