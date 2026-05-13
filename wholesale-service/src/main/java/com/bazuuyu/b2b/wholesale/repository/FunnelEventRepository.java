package com.bazuuyu.b2b.wholesale.repository;

import com.bazuuyu.b2b.wholesale.entity.FunnelEvent;
import com.bazuuyu.b2b.wholesale.entity.enums.FunnelEventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FunnelEventRepository extends JpaRepository<FunnelEvent, Long> {
    long countByEventType(FunnelEventType eventType);
}
