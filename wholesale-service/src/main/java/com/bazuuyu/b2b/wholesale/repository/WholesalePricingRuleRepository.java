package com.bazuuyu.b2b.wholesale.repository;

import com.bazuuyu.b2b.wholesale.entity.WholesalePricingRule;
import com.bazuuyu.b2b.wholesale.entity.enums.PriceChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WholesalePricingRuleRepository extends JpaRepository<WholesalePricingRule, Long> {
    List<WholesalePricingRule> findBySkuAndChannelAndActiveTrueOrderByMinQuantityDesc(String sku, PriceChannel channel);
}
