package com.raremarket.backend.controller;

import com.raremarket.backend.model.Shipping;
import com.raremarket.backend.service.ShippingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shippings")
public class ShippingController {
    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping
    public List<Shipping> getAllShippings() {
        return shippingService.getAllShippings();
    }

    @GetMapping("/{id}")
    public Shipping getShippingById(@PathVariable UUID id) {
        return shippingService.getShippingById(id);
    }

    @PostMapping
    public Shipping createShipping(@RequestBody Shipping shipping) {
        return shippingService.createShipping(shipping);
    }

    @DeleteMapping("/{id}")
    public void deleteShipping(@PathVariable UUID id) {
        shippingService.deleteShipping(id);
    }
}
