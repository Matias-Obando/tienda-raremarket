package com.raremarket.backend.controller;

import com.raremarket.backend.dto.item.ItemResponse;
import com.raremarket.backend.dto.item.ItemUpsertRequest;
import com.raremarket.backend.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponse> getAllItems(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String talla,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) UUID sellerId,
            @RequestParam(required = false) String sort
    ) {
        return itemService.listItems(query, categoria, talla, estado, minPrice, maxPrice, sellerId, sort);
    }

    @GetMapping("/{id}")
    public ItemResponse getItemById(@PathVariable String id) {
        return itemService.getItemById(id);
    }

    @PostMapping
    public ItemResponse createItem(@RequestBody ItemUpsertRequest request) {
        return itemService.createItem(request);
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@PathVariable String id, @RequestBody ItemUpsertRequest request) {
        return itemService.updateItem(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
