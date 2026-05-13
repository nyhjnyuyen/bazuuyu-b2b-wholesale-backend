package com.bazuuyu.b2b.wholesale.dto.response;

public record FunnelSummaryResponse(
        long adClicks,
        long registrationsSubmitted,
        long registrationsApproved,
        long ordersCreated,
        long ordersPaid,
        long repeatOrders
) {
}
