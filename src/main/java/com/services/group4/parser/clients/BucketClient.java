package com.services.group4.parser.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "asset-api", url = "http://parser-asset_service:8080/v1")
public interface BucketClient {

    @GetMapping("/asset/{container}/{key}")
    ResponseEntity<String> getSnippet(
            @PathVariable("container") String container, @PathVariable("key") Long key);

    @PutMapping("/asset/{container}/{key}")
    ResponseEntity<Void> saveSnippet(
            @PathVariable("container") String container,
            @PathVariable("key") Long key,
            @RequestBody String content);
}