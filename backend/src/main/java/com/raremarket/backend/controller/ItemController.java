package com.raremarket.backend.controller;

import com.raremarket.backend.dto.item.ItemResponse;
import com.raremarket.backend.dto.item.ItemImageCleanupRequest;
import com.raremarket.backend.dto.item.ItemUpsertRequest;
import com.raremarket.backend.service.SupabaseStorageService;
import com.raremarket.backend.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;
    private final SupabaseStorageService supabaseStorageService;

    public ItemController(ItemService itemService, SupabaseStorageService supabaseStorageService) {
        this.itemService = itemService;
        this.supabaseStorageService = supabaseStorageService;
    }

    @GetMapping
    public List<ItemResponse> getAllItems(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String subcategoria,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) String talla,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sellerId,
            @RequestParam(required = false) String sort
    ) {
        return itemService.listItems(query, categoria, subcategoria, genero, talla, estado, minPrice, maxPrice, sellerId, sort);
    }

    @GetMapping("/{id}")
    public ItemResponse getItemById(@PathVariable String id) {
        return itemService.getItemById(id);
    }

    @PostMapping
    public ItemResponse createItem(@Valid @RequestBody ItemUpsertRequest request, Authentication authentication) {
        return itemService.createItem(requireAuthenticatedUserId(authentication), request);
    }

    @PostMapping("/images")
    public ResponseEntity<Map<String, List<String>>> uploadItemImages(
            @RequestParam("files") List<MultipartFile> files,
            Authentication authentication
    ) {
        String userId = requireAuthenticatedUserId(authentication);
        List<String> urls = supabaseStorageService.uploadItemImages(files, userId)
                .stream()
                .map(SupabaseStorageService.UploadResult::publicUrl)
                .toList();

        return ResponseEntity.ok(Map.of("urls", urls));
    }

    @PostMapping("/images/cleanup")
    public ResponseEntity<Map<String, Integer>> cleanupUploadedImages(
            @RequestBody ItemImageCleanupRequest request,
            Authentication authentication
    ) {
        String userId = requireAuthenticatedUserId(authentication);
        int deleted = supabaseStorageService.deleteItemImagesForOwner(userId, request.getUrls());
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@PathVariable String id, @Valid @RequestBody ItemUpsertRequest request, Authentication authentication) {
        return itemService.updateItem(id, requireAuthenticatedUserId(authentication), request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id, Authentication authentication) {
        itemService.deleteItem(id, requireAuthenticatedUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    private String requireAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return authentication.getName().trim();
    }
}
