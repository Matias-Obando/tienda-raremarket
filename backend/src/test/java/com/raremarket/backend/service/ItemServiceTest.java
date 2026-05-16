package com.raremarket.backend.service;

import com.raremarket.backend.dto.item.ItemUpsertRequest;
import com.raremarket.backend.model.Item;
import com.raremarket.backend.repository.ItemRepository;
import com.raremarket.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    SupabaseStorageService supabaseStorageService;

    @InjectMocks
    ItemService itemService;

    private ItemUpsertRequest validRequest() {
        ItemUpsertRequest r = new ItemUpsertRequest();
        r.setTitulo("Titulo");
        r.setDescripcion("Desc");
        r.setPrecioEur(10.0);
        r.setCategoria("Cat");
        r.setSubcategoria("Sub");
        r.setGenero("M");
        r.setMarca("Marca");
        r.setTalla("L");
        r.setEstado("Nuevo");
        r.setImagen("http://img/1.jpg");
        r.setImages(List.of("http://img/1.jpg"));
        return r;
    }

    @Test
    @DisplayName("listItems throws BAD_REQUEST when minPrice is negative")
    void listItems_MinPriceNegative_Throws() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> itemService.listItems(null, null, null, null, null, null, -1.0, null, null, null));
        assertTrue(ex.getMessage().toLowerCase().contains("minprice"));
    }

    @Test
    @DisplayName("getItemById throws NOT_FOUND when missing")
    void getItemById_NotFound_Throws() {
        when(itemRepository.findById("missing")).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> itemService.getItemById("missing"));
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }

    @Test
    @DisplayName("createItem throws NOT_FOUND when user does not exist")
    void createItem_UserNotFound_Throws() {
        when(userRepository.existsById("user-1")).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> itemService.createItem("user-1", validRequest()));
        assertTrue(ex.getMessage().toLowerCase().contains("user not found"));
    }

    @Test
    @DisplayName("createItem propagates repository exceptions")
    void createItem_SaveThrows_Propagates() {
        when(userRepository.existsById("user-1")).thenReturn(true);
        when(itemRepository.save(any())).thenThrow(new RuntimeException("DB fail"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.createItem("user-1", validRequest()));
        assertTrue(ex.getMessage().contains("DB fail"));
    }

    @Test
    @DisplayName("deleteItem propagates delete failures and does not call storage")
    void deleteItem_DeleteThrows_SupabaseNotCalled() {
        Item item = new Item();
        item.setId("i1");
        item.setSellerId("user-1");
        item.setImagen("http://img/1.jpg");
        item.setImages(List.of("http://img/1.jpg"));

        when(itemRepository.findById("i1")).thenReturn(Optional.of(item));
        doThrow(new RuntimeException("delete fail")).when(itemRepository).delete(item);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.deleteItem("i1", "user-1"));
        assertTrue(ex.getMessage().contains("delete fail"));
        verify(supabaseStorageService, never()).deleteItemImagesForOwner(anyString(), anyList());
    }

    @Test
    @Disabled("Intentional failing test - disabled for CI")
    @DisplayName("Intentional failing test in ItemServiceTest")
    void intentionalFail_ItemService() {
        fail("Prueba intencional que debe fallar en ItemServiceTest");
    }
}
