package com.services.group4.parser.services;

import com.services.group4.parser.clients.BucketClient;
import lombok.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Generated
@Service
public class BlobStorageService {
    private final BucketClient bucketClient;

    @Autowired
    public BlobStorageService(BucketClient bucketClient) {
        this.bucketClient = bucketClient;
    }

    public void saveSnippet(String container, Long id, String content) {
        bucketClient.saveSnippet(container, id, content);
    }

    public Optional<String> getSnippet(String container, Long id) {
        ResponseEntity<String> response = bucketClient.getSnippet(container, id);
        if (response.hasBody()) {
            return Optional.ofNullable(response.getBody());
        }
        return Optional.empty();
    }
}
