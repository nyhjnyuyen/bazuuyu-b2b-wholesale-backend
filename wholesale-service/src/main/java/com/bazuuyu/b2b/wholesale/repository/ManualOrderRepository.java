package com.bazuuyu.b2b.wholesale.repository;

import com.bazuuyu.b2b.wholesale.entity.ManualOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManualOrderRepository extends JpaRepository<ManualOrder, Long> {
    Optional<ManualOrder> findByShopifyOrderId(Long shopifyOrderId);
}
