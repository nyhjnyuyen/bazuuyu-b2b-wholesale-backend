package com.bazuuyu.b2b.wholesale.dto.response;

import java.math.BigDecimal;

public record QuoteResponse(
        Long quoteId,
        String buyerEmail,
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal total
) {
}
