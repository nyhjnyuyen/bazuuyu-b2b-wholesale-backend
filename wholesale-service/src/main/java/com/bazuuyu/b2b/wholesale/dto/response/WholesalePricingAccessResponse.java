package com.bazuuyu.b2b.wholesale.dto.response;

import com.bazuuyu.b2b.wholesale.entity.enums.WholesaleStatus;

public record WholesalePricingAccessResponse(
        String email,
        boolean canViewPrice,
        boolean canPlaceOrder,
        WholesaleStatus status
) {
}
