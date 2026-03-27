package com.raremarket.backend.repository;

import com.raremarket.backend.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemRepository extends JpaRepository<Item, String>, JpaSpecificationExecutor<Item> {
}
