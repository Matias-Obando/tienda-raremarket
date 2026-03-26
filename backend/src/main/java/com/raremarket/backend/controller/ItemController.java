package com.raremarket.backend.controller;

import com.raremarket.backend.model.Item;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final Map<String, Item> items = new HashMap<>();

    // Constructor con algunos datos de ejemplo
    public ItemController() {
        Item item1 = new Item();
        item1.setId("1");
        item1.setTitulo("Sudadera Nike vintage");
        item1.setDescripcion("Sudadera cómoda, sin rotos. Pequeño desgaste normal.");
        item1.setPrecioEur(25);
        item1.setCategoria("Sudaderas");
        item1.setMarca("Nike");
        item1.setTalla("M");
        item1.setEstado("Usado");
        item1.setImagen("https://picsum.photos/seed/ropa1/800/800");
        item1.setImages(new String[]{
            "https://picsum.photos/seed/ropa1a/1200/900",
            "https://picsum.photos/seed/ropa1b/1200/900",
            "https://picsum.photos/seed/ropa1c/1200/900"
        });
        item1.setCreadoHace("hace 2 horas");
        items.put(item1.getId(), item1);

        Item item2 = new Item();
        item2.setId("2");
        item2.setTitulo("Camiseta básica blanca");
        item2.setDescripcion("Algodón, corte regular. Ideal para diario.");
        item2.setPrecioEur(8);
        item2.setCategoria("Camisetas");
        item2.setMarca("Zara");
        item2.setTalla("S");
        item2.setEstado("Como nuevo");
        item2.setImagen("https://picsum.photos/seed/ropa2/800/800");
        item2.setImages(new String[]{
            "https://picsum.photos/seed/ropa2a/1200/900",
            "https://picsum.photos/seed/ropa2b/1200/900",
            "https://picsum.photos/seed/ropa2c/1200/900"
        });
        item2.setCreadoHace("hace 1 día");
        items.put(item2.getId(), item2);
    }

    @GetMapping
    public Collection<Item> getAllItems() {
        return items.values();
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable String id) {
        return items.get(id);
    }

    @PostMapping
    public Item createItem(@RequestBody Item item) {
        String id = UUID.randomUUID().toString();
        item.setId(id);
        items.put(id, item);
        return item;
    }
}
