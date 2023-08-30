package com.github.bzalyaliev.uploadwebserver.websocket;

import com.github.bzalyaliev.uploadwebserver.model.ProgressUpdateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ProgressWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public ProgressWebSocketHandler(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @MessageMapping("/progress") // Отслеживание сообщений на этот адрес
    @SendTo("/topic/progress") // Отправка сообщений на этот адрес
    public void handleProgressUpdate(@Payload ProgressUpdateMessage progressUpdateMessage) {
        try {
            messagingTemplate.convertAndSend("/topic/progress", objectMapper.writeValueAsString(progressUpdateMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

