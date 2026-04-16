package com.raremarket.backend.service;

import com.raremarket.backend.model.Shipping;
import com.raremarket.backend.repository.ShippingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ShippingService {
    private final ShippingRepository shippingRepository;

    public ShippingService(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    public List<Shipping> getAllShippings() {
        return shippingRepository.findAll();
    }

    public Shipping getShippingById(UUID id) {
        return shippingRepository.findById(id).orElse(null);
    }

    public Shipping createShipping(Shipping shipping) {
        return shippingRepository.save(shipping);
    }

    public void deleteShipping(UUID id) {
        shippingRepository.deleteById(id);
    }
}
