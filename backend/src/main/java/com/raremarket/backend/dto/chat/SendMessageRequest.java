package com.raremarket.backend.dto.chat;

import java.util.UUID;

public class SendMessageRequest {
    private UUID senderId;
    private String content;

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
