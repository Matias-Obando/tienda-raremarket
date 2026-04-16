package com.raremarket.backend.repository;

import com.raremarket.backend.model.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ShippingRepository extends JpaRepository<Shipping, UUID> {
}
