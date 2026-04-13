package com.raremarket.backend.dto.chat;

import java.util.UUID;

public class CreateConversationRequest {
    private String itemId;
    private UUID buyerId;
    private UUID sellerId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public UUID getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(UUID buyerId) {
        this.buyerId = buyerId;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }
}
