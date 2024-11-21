package com.services.group4.parser.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.services.group4.parser.clients.BucketClient;
import com.services.group4.parser.services.BlobStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class BlobStorageServiceTest {

  @InjectMocks private BlobStorageService blobStorageService;

  @Mock private BucketClient bucketClient;

  @Test
  void test() {
    when(bucketClient.getSnippet(anyString(), anyLong()))
        .thenReturn(new ResponseEntity<>("Hello World", null, HttpStatusCode.valueOf(200)));
    assert blobStorageService.getSnippet("container", 1L).isPresent();
  }
}
