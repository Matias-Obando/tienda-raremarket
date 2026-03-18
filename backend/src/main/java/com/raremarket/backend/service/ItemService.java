package com.raremarket.backend.service;

import com.raremarket.backend.dto.CreateItemRequest;
import com.raremarket.backend.model.Item;
import com.raremarket.backend.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item getById(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado: " + id));
    }

    public Item create(CreateItemRequest request) {
        Item item = new Item();
        item.setTitulo(request.getTitulo());
        item.setDescripcion(request.getDescripcion());
        item.setPrecioEur(request.getPrecioEur());
        item.setCategoria(request.getCategoria());
        item.setMarca(request.getMarca());
        item.setTalla(request.getTalla());
        item.setEstado(request.getEstado());
        item.setImagen(request.getImagen());
        item.setCreadoHace("hace unos segundos");

        return itemRepository.save(item);
    }
}
