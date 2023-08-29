package com.github.bzalyaliev.uploadwebserver.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VideoUploadManager {
    private final Map<String, Integer> uploadCountMap = new HashMap<>();
    private static final int MAX_UPLOADS = 2;

    public boolean isUploadLimitExceeded(String username) {
        return uploadCountMap.getOrDefault(username, 0) >= MAX_UPLOADS;
    }

    public void incrementUploadCount(String username) {
        uploadCountMap.put(username, uploadCountMap.getOrDefault(username, 0) + 1);
    }

    public void resetUploadCount(String username) {
        uploadCountMap.remove(username);
    }
}
