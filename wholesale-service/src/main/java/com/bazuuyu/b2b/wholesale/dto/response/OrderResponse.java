package com.bazuuyu.b2b.wholesale.dto.response;

import com.bazuuyu.b2b.wholesale.entity.enums.OrderStatus;

import java.math.BigDecimal;

public record OrderResponse(
        Long orderId,
        Long quoteId,
        String buyerEmail,
        OrderStatus status,
        String trackingNumber,
        BigDecimal total
) {
}
