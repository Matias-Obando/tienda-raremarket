package com.raremarket.backend.dto.item;

import java.util.List;

public class ItemImageCleanupRequest {
    private List<String> urls;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
