package com.raremarket.backend.service;

import com.raremarket.backend.dto.item.ItemResponse;
import com.raremarket.backend.dto.item.ItemUpsertRequest;
import com.raremarket.backend.model.Item;
import com.raremarket.backend.repository.ItemRepository;
import com.raremarket.backend.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Stream;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final SupabaseStorageService supabaseStorageService;

    public ItemService(
            ItemRepository itemRepository,
            UserRepository userRepository,
            SupabaseStorageService supabaseStorageService
    ) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> listItems(
            String query,
            String categoria,
            String talla,
            String estado,
            Double minPrice,
            Double maxPrice,
                String sellerId,
            String sort
    ) {
        if (minPrice != null && minPrice < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPrice cannot be negative");
        }
        if (maxPrice != null && maxPrice < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxPrice cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPrice cannot be greater than maxPrice");
        }

        List<Specification<Item>> specs = Stream.of(
            searchSpecification(query),
            equalsIgnoreCase("categoria", categoria),
            equalsIgnoreCase("talla", talla),
            equalsIgnoreCase("estado", estado),
            sellerId == null ? null : (root, ignoredQuery, cb) -> cb.equal(root.get("sellerId"), sellerId),
            minPrice == null ? null : (root, ignoredQuery, cb) -> cb.greaterThanOrEqualTo(root.get("precioEur"), minPrice),
            maxPrice == null ? null : (root, ignoredQuery, cb) -> cb.lessThanOrEqualTo(root.get("precioEur"), maxPrice)
        ).filter(s -> s != null).toList();
        Specification<Item> specification = specs.isEmpty() ? null : Specification.allOf(specs.toArray(new Specification[0]));

        List<Item> items = specification == null
            ? itemRepository.findAll(resolveSort(sort))
            : itemRepository.findAll(specification, resolveSort(sort));

        return items.stream()
                .map(ItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemById(String id) {
        return ItemResponse.from(findItem(id));
    }

    @Transactional
    public ItemResponse createItem(String authenticatedUserId, ItemUpsertRequest request) {
        ensureUserExists(authenticatedUserId);

        Item item = new Item();
        item.setSellerId(authenticatedUserId);
        applyRequest(item, request, true);
        return ItemResponse.from(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse updateItem(String id, String authenticatedUserId, ItemUpsertRequest request) {
        Item item = findItem(id);
        ensureItemOwnership(item, authenticatedUserId);
        applyRequest(item, request, false);
        return ItemResponse.from(itemRepository.save(item));
    }

    @Transactional
    public void deleteItem(String id, String authenticatedUserId) {
        Item item = findItem(id);
        ensureItemOwnership(item, authenticatedUserId);

        List<String> itemImageUrls = collectItemImageUrls(item);
        itemRepository.delete(item);
        supabaseStorageService.deleteItemImagesForOwner(authenticatedUserId, itemImageUrls);
    }

    private Item findItem(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
    }

    private void ensureUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    private void applyRequest(Item item, ItemUpsertRequest request, boolean isCreate) {
        item.setTitulo(requireText(request.getTitulo(), "titulo"));
        item.setDescripcion(requireText(request.getDescripcion(), "descripcion"));
        item.setCategoria(requireText(request.getCategoria(), "categoria"));
        item.setMarca(requireText(request.getMarca(), "marca"));
        item.setTalla(requireText(request.getTalla(), "talla"));
        item.setEstado(requireText(request.getEstado(), "estado"));

        if (request.getPrecioEur() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "precioEur cannot be negative");
        }
        item.setPrecioEur(request.getPrecioEur());

        List<String> normalizedImages = request.getImages() == null
                ? List.of()
                : request.getImages().stream()
                .map(url -> url == null ? "" : url.trim())
                .filter(url -> !url.isEmpty())
                .toList();

        String mainImage = request.getImagen() == null ? "" : request.getImagen().trim();
        if (mainImage.isEmpty() && !normalizedImages.isEmpty()) {
            mainImage = normalizedImages.get(0);
        }
        if (mainImage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "imagen is required");
        }

        item.setImagen(mainImage);
        item.setImages(normalizedImages);
    }

    private void ensureItemOwnership(Item item, String authenticatedUserId) {
        if (item.getSellerId() == null || !item.getSellerId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own items");
        }
    }

    private List<String> collectItemImageUrls(Item item) {
        Set<String> urls = new LinkedHashSet<>();

        if (item.getImagen() != null && !item.getImagen().isBlank()) {
            urls.add(item.getImagen().trim());
        }

        if (item.getImages() != null) {
            item.getImages().stream()
                    .filter(url -> url != null && !url.isBlank())
                    .map(String::trim)
                    .forEach(urls::add);
        }

        return List.copyOf(urls);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        return value.trim();
    }

    private Specification<Item> containsIgnoreCase(String field, String term) {
        if (term == null || term.trim().isEmpty()) {
            return null;
        }
        String likeValue = "%" + term.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get(field)), likeValue);
    }

    private Specification<Item> searchSpecification(String term) {
        if (term == null || term.trim().isEmpty()) {
            return null;
        }

        return Specification.anyOf(
                containsIgnoreCase("titulo", term),
                containsIgnoreCase("descripcion", term),
                containsIgnoreCase("marca", term),
                containsIgnoreCase("categoria", term)
        );
    }

    private Specification<Item> equalsIgnoreCase(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(cb.lower(root.get(field)), value.trim().toLowerCase());
    }

    private Sort resolveSort(String sort) {
        if ("price_asc".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.ASC, "precioEur");
        }
        if ("price_desc".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "precioEur");
        }
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}
