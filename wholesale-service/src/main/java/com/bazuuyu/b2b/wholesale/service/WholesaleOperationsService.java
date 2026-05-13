package com.bazuuyu.b2b.wholesale.service;

import com.bazuuyu.b2b.core.exception.NotFoundException;
import com.bazuuyu.b2b.wholesale.dto.request.*;
import com.bazuuyu.b2b.wholesale.dto.response.FunnelSummaryResponse;
import com.bazuuyu.b2b.wholesale.dto.response.OrderResponse;
import com.bazuuyu.b2b.wholesale.dto.response.QuoteResponse;
import com.bazuuyu.b2b.wholesale.entity.*;
import com.bazuuyu.b2b.wholesale.entity.enums.FunnelEventType;
import com.bazuuyu.b2b.wholesale.entity.enums.LeadStage;
import com.bazuuyu.b2b.wholesale.entity.enums.OrderStatus;
import com.bazuuyu.b2b.wholesale.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
public class WholesaleOperationsService {

    private final WholesalePricingRuleRepository pricingRuleRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final WholesaleQuoteRepository wholesaleQuoteRepository;
    private final ManualOrderRepository manualOrderRepository;
    private final LeadRecordRepository leadRecordRepository;
    private final FunnelEventRepository funnelEventRepository;

    public WholesaleOperationsService(
            WholesalePricingRuleRepository pricingRuleRepository,
            ShippingPolicyRepository shippingPolicyRepository,
            InventoryRecordRepository inventoryRecordRepository,
            WholesaleQuoteRepository wholesaleQuoteRepository,
            ManualOrderRepository manualOrderRepository,
            LeadRecordRepository leadRecordRepository,
            FunnelEventRepository funnelEventRepository
    ) {
        this.pricingRuleRepository = pricingRuleRepository;
        this.shippingPolicyRepository = shippingPolicyRepository;
        this.inventoryRecordRepository = inventoryRecordRepository;
        this.wholesaleQuoteRepository = wholesaleQuoteRepository;
        this.manualOrderRepository = manualOrderRepository;
        this.leadRecordRepository = leadRecordRepository;
        this.funnelEventRepository = funnelEventRepository;
    }

    @Transactional
    public WholesalePricingRule upsertPricingRule(UpsertPricingRuleRequest request) {
        WholesalePricingRule rule = new WholesalePricingRule();
        rule.setSku(request.getSku().trim().toUpperCase());
        rule.setChannel(request.getChannel());
        rule.setMinQuantity(request.getMinQuantity());
        rule.setUnitPrice(request.getUnitPrice().setScale(2, RoundingMode.HALF_UP));
        rule.setActive(true);
        return pricingRuleRepository.save(rule);
    }

    @Transactional
    public ShippingPolicy upsertShippingPolicy(UpsertShippingPolicyRequest request) {
        ShippingPolicy policy = shippingPolicyRepository.findTopByModeAndActiveTrue(request.getMode())
                .orElseGet(ShippingPolicy::new);
        policy.setMode(request.getMode());
        policy.setFreeShippingThreshold(scale(request.getFreeShippingThreshold()));
        policy.setFlatShippingFee(scale(request.getFlatShippingFee()));
        policy.setPerKgFee(scale(request.getPerKgFee()));
        policy.setActive(true);
        return shippingPolicyRepository.save(policy);
    }

    @Transactional
    public InventoryRecord upsertInventory(UpsertInventoryRequest request) {
        InventoryRecord record = inventoryRecordRepository.findBySku(request.getSku().trim().toUpperCase())
                .orElseGet(InventoryRecord::new);
        record.setSku(request.getSku().trim().toUpperCase());
        record.setAvailableQuantity(request.getAvailableQuantity());
        record.setSourceMode(request.getSourceMode());
        record.setEtaDays(request.getEtaDays());
        record.setActive(true);
        return inventoryRecordRepository.save(record);
    }

    @Transactional
    public QuoteResponse createQuote(CreateQuoteRequest request) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (QuoteLineRequest line : request.getItems()) {
            String sku = line.getSku().trim().toUpperCase();
            WholesalePricingRule rule = resolvePriceRule(sku, request);
            subtotal = subtotal.add(rule.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity())));
            totalWeight = totalWeight.add((line.getWeightKg() == null ? BigDecimal.ZERO : line.getWeightKg())
                    .multiply(BigDecimal.valueOf(line.getQuantity())));
        }

        ShippingPolicy policy = shippingPolicyRepository.findTopByModeAndActiveTrue(request.getShipmentMode())
                .orElseThrow(() -> new NotFoundException("SHIPPING_POLICY_NOT_FOUND", "Shipping policy not configured."));

        BigDecimal shippingFee = subtotal.compareTo(policy.getFreeShippingThreshold()) >= 0
                ? BigDecimal.ZERO
                : policy.getFlatShippingFee().add(policy.getPerKgFee().multiply(totalWeight));

        WholesaleQuote quote = new WholesaleQuote();
        quote.setBuyerEmail(request.getBuyerEmail().trim().toLowerCase());
        quote.setPriceChannel(request.getPriceChannel());
        quote.setShipmentMode(request.getShipmentMode());
        quote.setSubtotal(scale(subtotal));
        quote.setShippingFee(scale(shippingFee));
        quote.setTotal(scale(subtotal.add(shippingFee)));

        WholesaleQuote saved = wholesaleQuoteRepository.save(quote);

        trackFunnelEventInternal(saved.getBuyerEmail(), FunnelEventType.ORDER_CREATED, "quote");

        return new QuoteResponse(saved.getId(), saved.getBuyerEmail(), saved.getSubtotal(), saved.getShippingFee(), saved.getTotal());
    }

    @Transactional
    public OrderResponse convertQuoteToOrder(ConvertQuoteToOrderRequest request) {
        WholesaleQuote quote = wholesaleQuoteRepository.findById(request.getQuoteId())
                .orElseThrow(() -> new NotFoundException("QUOTE_NOT_FOUND", "Quote not found."));

        ManualOrder order = new ManualOrder();
        order.setQuoteId(quote.getId());
        order.setBuyerEmail(request.getBuyerEmail().trim().toLowerCase());
        order.setStatus(OrderStatus.SUBMITTED);
        order.setTotal(quote.getTotal());

        quote.setConvertedToOrder(true);
        wholesaleQuoteRepository.save(quote);
        ManualOrder saved = manualOrderRepository.save(order);

        return toOrderResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        ManualOrder order = manualOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("ORDER_NOT_FOUND", "Order not found."));

        order.setStatus(request.getStatus());
        if (request.getTrackingNumber() != null && !request.getTrackingNumber().isBlank()) {
            order.setTrackingNumber(request.getTrackingNumber().trim());
        }
        ManualOrder saved = manualOrderRepository.save(order);

        if (saved.getStatus() == OrderStatus.PAID) {
            trackFunnelEventInternal(saved.getBuyerEmail(), FunnelEventType.ORDER_PAID, "order-status");
        }

        return toOrderResponse(saved);
    }

    public InventoryRecord getInventoryBySku(String sku) {
        return inventoryRecordRepository.findBySku(sku.trim().toUpperCase())
                .orElseThrow(() -> new NotFoundException("INVENTORY_NOT_FOUND", "Inventory record not found."));
    }

    @Transactional
    public LeadRecord upsertLead(UpsertLeadRequest request) {
        LeadRecord lead = leadRecordRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseGet(LeadRecord::new);
        lead.setEmail(request.getEmail().trim().toLowerCase());
        lead.setCompanyName(request.getCompanyName());
        lead.setStage(request.getStage() == null ? LeadStage.NEW : request.getStage());
        lead.setNote(request.getNote());
        return leadRecordRepository.save(lead);
    }

    @Transactional
    public FunnelEvent trackFunnelEvent(TrackFunnelEventRequest request) {
        return trackFunnelEventInternal(request.getEmail().trim().toLowerCase(), request.getEventType(), request.getSource());
    }

    public FunnelSummaryResponse getFunnelSummary() {
        return new FunnelSummaryResponse(
                funnelEventRepository.countByEventType(FunnelEventType.AD_CLICK),
                funnelEventRepository.countByEventType(FunnelEventType.REGISTRATION_SUBMITTED),
                funnelEventRepository.countByEventType(FunnelEventType.REGISTRATION_APPROVED),
                funnelEventRepository.countByEventType(FunnelEventType.ORDER_CREATED),
                funnelEventRepository.countByEventType(FunnelEventType.ORDER_PAID),
                funnelEventRepository.countByEventType(FunnelEventType.REPEAT_ORDER)
        );
    }

    private WholesalePricingRule resolvePriceRule(String sku, CreateQuoteRequest request) {
        Integer quantity = request.getItems().stream()
                .filter(item -> sku.equalsIgnoreCase(item.getSku().trim()))
                .map(QuoteLineRequest::getQuantity)
                .findFirst()
                .orElse(0);

        return pricingRuleRepository.findBySkuAndChannelAndActiveTrueOrderByMinQuantityDesc(sku, request.getPriceChannel())
                .stream()
                .filter(rule -> rule.getMinQuantity() <= quantity)
                .max(Comparator.comparing(WholesalePricingRule::getMinQuantity))
                .orElseThrow(() -> new NotFoundException("PRICING_RULE_NOT_FOUND", "No pricing rule for sku=" + sku));
    }

    private FunnelEvent trackFunnelEventInternal(String email, FunnelEventType type, String source) {
        FunnelEvent event = new FunnelEvent();
        event.setEmail(email);
        event.setEventType(type);
        event.setSource(source);
        return funnelEventRepository.save(event);
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private OrderResponse toOrderResponse(ManualOrder saved) {
        return new OrderResponse(
                saved.getId(),
                saved.getQuoteId(),
                saved.getBuyerEmail(),
                saved.getStatus(),
                saved.getTrackingNumber(),
                saved.getTotal()
        );
    }
}
