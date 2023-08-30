package com.github.bzalyaliev.uploadwebserver.controller;

import com.github.bzalyaliev.uploadwebserver.model.ProgressUpdateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final List<ProgressUpdateMessage> activeUploads = new ArrayList<>();

    @MessageMapping("/topic/progress-start")
    public void handleStartProgress(ProgressUpdateMessage message) {
        log.info("Received start progress message: {}", message);
        activeUploads.add(message);
    }

    @MessageMapping("/topic/progress")
    public void handleProgress(ProgressUpdateMessage message) {
        log.info("Received progress update message: {}", message);
        activeUploads.add(message);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<List<ProgressUpdateMessage>> getDashboard() {
        return ResponseEntity.ok(activeUploads);
    }

}

