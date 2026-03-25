# Ejemplos de Código: Usando Supabase en Spring Boot

## Ejemplo 1: Crear una entidad JPA (Modelo)

```java
// src/main/java/com/raremarket/backend/model/Item.java
package com.raremarket.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false)
    private Double precio;
    
    @Column(name = "url_imagen")
    private String urlImagen;
    
    @Column(name = "nombre_vendedor")
    private String nombreVendedor;
    
    @Column(nullable = false)
    private String categoria;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime creadoEn;
    
    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }
}
```

## Ejemplo 2: Repositorio JPA

```java
// src/main/java/com/raremarket/backend/repository/ItemRepository.java
package com.raremarket.backend.repository;

import com.raremarket.backend.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    // Métodos personalizados
    List<Item> findByCategoria(String categoria);
    List<Item> findByNombreContainingIgnoreCase(String nombre);
    Optional<Item> findByNombreVendedor(String nombreVendedor);
}
```

## Ejemplo 3: Servicio

```java
// src/main/java/com/raremarket/backend/service/ItemService.java
package com.raremarket.backend.service;

import com.raremarket.backend.model.Item;
import com.raremarket.backend.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    // Obtener todos los items
    public List<Item> obtenerTodos() {
        return itemRepository.findAll();
    }
    
    // Obtener por ID
    public Optional<Item> obtenerPorId(Long id) {
        return itemRepository.findById(id);
    }
    
    // Obtener por categoría
    public List<Item> obtenerPorCategoria(String categoria) {
        return itemRepository.findByCategoria(categoria);
    }
    
    // Crear item
    public Item crearItem(Item item) {
        return itemRepository.save(item);
    }
    
    // Actualizar item
    public Item actualizarItem(Long id, Item itemActualizado) {
        Optional<Item> item = itemRepository.findById(id);
        
        if (item.isPresent()) {
            Item existente = item.get();
            existente.setNombre(itemActualizado.getNombre());
            existente.setDescripcion(itemActualizado.getDescripcion());
            existente.setPrecio(itemActualizado.getPrecio());
            existente.setUrlImagen(itemActualizado.getUrlImagen());
            existente.setCategoria(itemActualizado.getCategoria());
            
            return itemRepository.save(existente);
        }
        
        return null;
    }
    
    // Eliminar item
    public void eliminarItem(Long id) {
        itemRepository.deleteById(id);
    }
}
```

## Ejemplo 4: Controlador REST

```java
// src/main/java/com/raremarket/backend/controller/ItemController.java
package com.raremarket.backend.controller;

import com.raremarket.backend.model.Item;
import com.raremarket.backend.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:3000")
public class ItemController {
    
    @Autowired
    private ItemService itemService;
    
    // GET /api/items - Obtener todos los items
    @GetMapping
    public ResponseEntity<List<Item>> obtenerTodos() {
        List<Item> items = itemService.obtenerTodos();
        return ResponseEntity.ok(items);
    }
    
    // GET /api/items/{id} - Obtener item por ID
    @GetMapping("/{id}")
    public ResponseEntity<Item> obtenerPorId(@PathVariable Long id) {
        Optional<Item> item = itemService.obtenerPorId(id);
        return item.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // GET /api/items/categoria/{categoria} - Obtener por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Item>> obtenerPorCategoria(@PathVariable String categoria) {
        List<Item> items = itemService.obtenerPorCategoria(categoria);
        return ResponseEntity.ok(items);
    }
    
    // POST /api/items - Crear nuevo item
    @PostMapping
    public ResponseEntity<Item> crearItem(@RequestBody Item item) {
        Item nuevoItem = itemService.crearItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoItem);
    }
    
    // PUT /api/items/{id} - Actualizar item
    @PutMapping("/{id}")
    public ResponseEntity<Item> actualizarItem(
            @PathVariable Long id,
            @RequestBody Item item) {
        Item actualizado = itemService.actualizarItem(id, item);
        
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }
    
    // DELETE /api/items/{id} - Eliminar item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        itemService.eliminarItem(id);
        return ResponseEntity.noContent().build();
    }
}
```

## Ejemplo 5: DTO (Data Transfer Object)

```java
// src/main/java/com/raremarket/backend/dto/ItemDTO.java
package com.raremarket.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String urlImagen;
    private String nombreVendedor;
    private String categoria;
    private String creadoEn;
}
```

## Ejemplo 6: Llamadas desde Frontend Nuxt

```javascript
// app/components/ItemList.vue
<template>
  <div class="items-container">
    <div v-for="item in items" :key="item.id" class="item-card">
      <h3>{{ item.nombre }}</h3>
      <p>{{ item.descripcion }}</p>
      <p class="price">${{ item.precio }}</p>
      <button @click="eliminar(item.id)">Eliminar</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Item {
  id: number
  nombre: string
  descripcion: string
  precio: number
  urlImagen: string
  nombreVendedor: string
  categoria: string
}

const items = ref<Item[]>([])
const apiUrl = 'http://localhost:8080/api/items'

// Obtener todos los items
const cargarItems = async () => {
  try {
    const response = await fetch(apiUrl)
    if (!response.ok) throw new Error('Error al cargar items')
    items.value = await response.json()
  } catch (error) {
    console.error('Error:', error)
  }
}

// Crear nuevo item
const crearItem = async () => {
  const nuevoItem = {
    nombre: 'Nuevo producto',
    descripcion: 'Descripción',
    precio: 99.99,
    categoria: 'electrónica'
  }
  
  try {
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(nuevoItem)
    })
    
    if (response.ok) {
      const item = await response.json()
      items.value.push(item)
    }
  } catch (error) {
    console.error('Error:', error)
  }
}

// Eliminar item
const eliminar = async (id: number) => {
  try {
    const response = await fetch(`${apiUrl}/${id}`, {
      method: 'DELETE'
    })
    
    if (response.ok) {
      items.value = items.value.filter(item => item.id !== id)
    }
  } catch (error) {
    console.error('Error:', error)
  }
}

onMounted(() => {
  cargarItems()
})
</script>

<style scoped>
.items-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.item-card {
  border: 1px solid #ddd;
  padding: 15px;
  border-radius: 8px;
}

.price {
  color: #e74c3c;
  font-weight: bold;
  font-size: 1.2em;
}
</style>
```

## Ejemplo 7: Generar Entidades desde Base de Datos

Si ya tienes tablas en Supabase, puedes generar entidades automáticamente:

```bash
# Usar JPA Buddy en IntelliJ IDEA o similar
# O generar manualmente con annotations:

@Entity
@Table(name = "tu_tabla")
public class TuEntidad {
    // Tus propiedades...
}
```

## Ejemplo 8: Configuración de Transacciones

```java
// Para operaciones complejas que afectan múltiples tablas
@Service
public class VentaService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Transactional  // Toda la operación es una transacción
    public Venta procesarVenta(Long itemId, int cantidad) {
        // Si algo falla, se hace rollback de todo
        Item item = itemRepository.findById(itemId).orElseThrow();
        Venta venta = new Venta(item, cantidad);
        return ventaRepository.save(venta);
    }
}
```

---

## Pasos para copiar estos ejemplos:

1. Copia el código anterior a los archivos correspondientes
2. Reemplaza los import statements si es necesario
3. Ejecuta `mvn clean install`
4. Inicia el backend: `mvn spring-boot:run`
5. Prueba desde Postman o desde tu frontend Nuxt

## Testing con Postman

```
POST http://localhost:8080/api/items
Content-Type: application/json

{
  "nombre": "Laptop Gaming",
  "descripcion": "Laptop de alto rendimiento",
  "precio": 1299.99,
  "urlImagen": "https://...",
  "nombreVendedor": "Juan",
  "categoria": "electrónica"
}
```

---

Para más información, revisa:
- SUPABASE_SETUP.md
- ARQUITECTURA_SUPABASE.md

