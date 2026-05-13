package com.bazuuyu.b2b.wholesale.repository;

import com.bazuuyu.b2b.wholesale.entity.ShippingPolicy;
import com.bazuuyu.b2b.wholesale.entity.enums.ShipmentMode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingPolicyRepository extends JpaRepository<ShippingPolicy, Long> {
    Optional<ShippingPolicy> findTopByModeAndActiveTrue(ShipmentMode mode);
}
