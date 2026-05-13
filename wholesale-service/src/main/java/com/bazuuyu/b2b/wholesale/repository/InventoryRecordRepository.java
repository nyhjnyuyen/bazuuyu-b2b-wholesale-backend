package com.bazuuyu.b2b.wholesale.repository;

import com.bazuuyu.b2b.wholesale.entity.InventoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, Long> {
    Optional<InventoryRecord> findBySku(String sku);
}
