package com.bazuuyu.b2b.wholesale.controller;

import com.bazuuyu.b2b.core.dto.ApiResponse;
import com.bazuuyu.b2b.wholesale.dto.request.*;
import com.bazuuyu.b2b.wholesale.dto.response.FunnelSummaryResponse;
import com.bazuuyu.b2b.wholesale.dto.response.OrderResponse;
import com.bazuuyu.b2b.wholesale.dto.response.QuoteResponse;
import com.bazuuyu.b2b.wholesale.entity.*;
import com.bazuuyu.b2b.wholesale.service.WholesaleOperationsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wholesale/ops")
public class WholesaleOperationsController {

    private final WholesaleOperationsService operationsService;

    public WholesaleOperationsController(WholesaleOperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @PostMapping("/pricing-rules")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<WholesalePricingRule>> upsertPricingRule(@Valid @RequestBody UpsertPricingRuleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Pricing rule saved.", operationsService.upsertPricingRule(request)));
    }

    @PostMapping("/shipping-policies")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<ShippingPolicy>> upsertShippingPolicy(@Valid @RequestBody UpsertShippingPolicyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Shipping policy saved.", operationsService.upsertShippingPolicy(request)));
    }

    @PostMapping("/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<InventoryRecord>> upsertInventory(@Valid @RequestBody UpsertInventoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Inventory saved.", operationsService.upsertInventory(request)));
    }

    @GetMapping("/inventory/{sku}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<InventoryRecord>> getInventory(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.success("Inventory loaded.", operationsService.getInventoryBySku(sku)));
    }

    @PostMapping("/quotes")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'WHOLESALE_BUYER')")
    public ResponseEntity<ApiResponse<QuoteResponse>> createQuote(@Valid @RequestBody CreateQuoteRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Quote generated.", operationsService.createQuote(request)));
    }

    @PostMapping("/orders/convert")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<OrderResponse>> convertQuoteToOrder(@Valid @RequestBody ConvertQuoteToOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Quote converted to order.", operationsService.convertQuoteToOrder(request)));
    }

    @PatchMapping("/orders/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated.", operationsService.updateOrderStatus(orderId, request)));
    }

    @PostMapping("/leads")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<LeadRecord>> upsertLead(@Valid @RequestBody UpsertLeadRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Lead saved.", operationsService.upsertLead(request)));
    }

    @PostMapping("/funnel-events")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FunnelEvent>> trackFunnelEvent(@Valid @RequestBody TrackFunnelEventRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Funnel event tracked.", operationsService.trackFunnelEvent(request)));
    }

    @GetMapping("/funnel-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<FunnelSummaryResponse>> funnelSummary() {
        return ResponseEntity.ok(ApiResponse.success("Funnel summary loaded.", operationsService.getFunnelSummary()));
    }
}
