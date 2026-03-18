package com.raremarket.backend.repository;

import com.raremarket.backend.model.Categoria;
import com.raremarket.backend.model.Estado;
import com.raremarket.backend.model.Item;
import com.raremarket.backend.model.Talla;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ItemRepository {

    private final Map<String, Item> items = new ConcurrentHashMap<>();

    public ItemRepository() {
        seed();
    }

    public List<Item> findAll() {
        List<Item> values = new ArrayList<>(items.values());
        values.sort(Comparator.comparing(Item::getId));
        return values;
    }

    public Optional<Item> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    public Item save(Item item) {
        if (item.getId() == null || item.getId().isBlank()) {
            item.setId(UUID.randomUUID().toString());
        }
        items.put(item.getId(), item);
        return item;
    }

    private void seed() {
        save(new Item("1", "Sudadera Nike vintage", "Sudadera comoda, sin rotos. Pequeno desgaste normal.",
                25, Categoria.SUDADERAS, "Nike", Talla.M, Estado.USADO,
                "https://picsum.photos/seed/ropa1/800/800", "hace 2 horas"));
        save(new Item("2", "Camiseta basica blanca", "Algodon, corte regular. Ideal para diario.",
                8, Categoria.CAMISETAS, "Zara", Talla.S, Estado.COMO_NUEVO,
                "https://picsum.photos/seed/ropa2/800/800", "hace 1 dia"));
        save(new Item("3", "Chaqueta vaquera oversize", "Vaquera clasica, estilo oversize.",
                30, Categoria.CHAQUETAS, "Pull&Bear", Talla.L, Estado.USADO,
                "https://picsum.photos/seed/ropa3/800/800", "hace 3 dias"));
        save(new Item("4", "Pantalon cargo negro", "Cargo con bolsillos. Muy comodo.",
                18, Categoria.PANTALONES, "Bershka", Talla.M, Estado.COMO_NUEVO,
                "https://picsum.photos/seed/ropa4/800/800", "hace 5 horas"));
        save(new Item("5", "Zapatillas Adidas", "Usadas pero bien cuidadas.",
                22, Categoria.ZAPATILLAS, "Adidas", Talla.L, Estado.USADO,
                "https://picsum.photos/seed/ropa5/800/800", "hace 4 dias"));
    }
}
