package com.services.group4.parser.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "asset-api", url = "http://asset_service:8080/v1")
public interface BucketClient {
  @GetMapping("/asset/{container}/{key}")
  ResponseEntity<String> getSnippet(
      @PathVariable("container") String container, @PathVariable("key") Long key);
}
