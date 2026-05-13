package com.bazuuyu.b2b.wholesale.service;

import com.bazuuyu.b2b.core.exception.ForbiddenException;
import com.bazuuyu.b2b.wholesale.entity.ManualOrder;
import com.bazuuyu.b2b.wholesale.entity.ProcessedWebhookEvent;
import com.bazuuyu.b2b.wholesale.entity.WholesaleAccount;
import com.bazuuyu.b2b.wholesale.entity.enums.OrderStatus;
import com.bazuuyu.b2b.wholesale.entity.enums.WholesaleStatus;
import com.bazuuyu.b2b.wholesale.repository.ManualOrderRepository;
import com.bazuuyu.b2b.wholesale.repository.ProcessedWebhookEventRepository;
import com.bazuuyu.b2b.wholesale.repository.WholesaleAccountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Service
public class ShopifyWebhookService {

    private final ManualOrderRepository manualOrderRepository;
    private final ProcessedWebhookEventRepository processedWebhookEventRepository;
    private final WholesaleAccountRepository wholesaleAccountRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.shopify.webhook-secret:}")
    private String webhookSecret;
    @Value("${app.shopify.wholesale-approved-tag:WHOLESALE_APPROVED}")
    private String wholesaleApprovedTag;
    @Value("${app.shopify.wholesale-can-order-tag:WHOLESALE_CAN_ORDER}")
    private String wholesaleCanOrderTag;
    @Value("${app.shopify.wholesale-suspended-tag:WHOLESALE_SUSPENDED}")
    private String wholesaleSuspendedTag;

    public ShopifyWebhookService(
            ManualOrderRepository manualOrderRepository,
            ProcessedWebhookEventRepository processedWebhookEventRepository,
            WholesaleAccountRepository wholesaleAccountRepository,
            ObjectMapper objectMapper
    ) {
        this.manualOrderRepository = manualOrderRepository;
        this.processedWebhookEventRepository = processedWebhookEventRepository;
        this.wholesaleAccountRepository = wholesaleAccountRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void processWebhook(String topic, String hmacHeader, String webhookId, String payload) {
        verifyHmac(hmacHeader, payload);
        ProcessedWebhookEvent marker = registerWebhook(topic, webhookId);
        if (marker == null) {
            return;
        }

        try {
            JsonNode root = objectMapper.readTree(payload);
            if ("orders/paid".equalsIgnoreCase(topic) || "orders/create".equalsIgnoreCase(topic)) {
                handleOrderPaid(root);
                return;
            }
            if ("fulfillments/create".equalsIgnoreCase(topic)) {
                handleFulfillment(root);
                return;
            }
            if ("orders/cancelled".equalsIgnoreCase(topic)) {
                handleOrderCancelled(root);
            } else if ("customers/create".equalsIgnoreCase(topic) || "customers/update".equalsIgnoreCase(topic)) {
                handleCustomerSync(root);
            }
            marker.setProcessed(true);
            marker.setProcessedAt(LocalDateTime.now());
            processedWebhookEventRepository.save(marker);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Shopify webhook payload.", e);
        }
    }

    private ProcessedWebhookEvent registerWebhook(String topic, String webhookId) {
        if (webhookId == null || webhookId.isBlank()) {
            throw new ForbiddenException("SHOPIFY_WEBHOOK_ID_MISSING", "Missing Shopify webhook id.");
        }
        String normalizedId = webhookId.trim();
        if (processedWebhookEventRepository.findByWebhookId(normalizedId).isPresent()) {
            return null;
        }

        ProcessedWebhookEvent marker = new ProcessedWebhookEvent();
        marker.setWebhookId(normalizedId);
        marker.setTopic(topic == null ? "" : topic);
        marker.setProcessed(false);
        try {
            return processedWebhookEventRepository.saveAndFlush(marker);
        } catch (DataIntegrityViolationException ex) {
            return null;
        }
    }

    private void handleOrderPaid(JsonNode root) {
        Long orderId = root.path("id").asLong(0);
        if (orderId == 0) {
            return;
        }

        ManualOrder order = manualOrderRepository.findByShopifyOrderId(orderId).orElseGet(ManualOrder::new);
        if (order.getQuoteId() == null) {
            order.setQuoteId(0L);
        }

        String email = root.path("email").asText("").trim().toLowerCase();
        if (!email.isBlank()) {
            order.setBuyerEmail(email);
        }

        String financialStatus = root.path("financial_status").asText("").toLowerCase();
        if ("paid".equals(financialStatus) || "partially_paid".equals(financialStatus)) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.SUBMITTED);
        }

        String totalPrice = root.path("total_price").asText("0");
        order.setTotal(parseAmount(totalPrice));
        order.setShopifyOrderId(orderId);

        manualOrderRepository.save(order);
    }

    private void handleFulfillment(JsonNode root) {
        Long orderId = root.path("order_id").asLong(0);
        if (orderId == 0) {
            return;
        }

        manualOrderRepository.findByShopifyOrderId(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.SHIPPED);
            String tracking = root.path("tracking_number").asText("");
            if (tracking.isBlank() && root.path("tracking_numbers").isArray() && root.path("tracking_numbers").size() > 0) {
                tracking = root.path("tracking_numbers").get(0).asText("");
            }
            if (!tracking.isBlank()) {
                order.setTrackingNumber(tracking);
            }
            manualOrderRepository.save(order);
        });
    }

    private void handleOrderCancelled(JsonNode root) {
        Long orderId = root.path("id").asLong(0);
        if (orderId == 0) {
            return;
        }

        manualOrderRepository.findByShopifyOrderId(orderId).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            manualOrderRepository.save(order);
        });
    }

    private void handleCustomerSync(JsonNode root) {
        Long customerId = root.path("id").asLong(0);
        String email = root.path("email").asText("").trim().toLowerCase();
        if (customerId == 0 || email.isBlank()) {
            return;
        }

        Set<String> tags = parseTags(root.path("tags").asText(""));
        boolean approvedByTag = tags.contains(wholesaleApprovedTag.toUpperCase());
        boolean canOrderByTag = tags.contains(wholesaleCanOrderTag.toUpperCase());
        boolean suspendedByTag = tags.contains(wholesaleSuspendedTag.toUpperCase());

        boolean approvedByMetafield = extractBooleanMetafield(root, "approved");
        boolean canOrderByMetafield = extractBooleanMetafield(root, "can_order");

        boolean approved = approvedByTag || approvedByMetafield;
        boolean canOrder = canOrderByTag || canOrderByMetafield || approved;

        WholesaleAccount account = wholesaleAccountRepository.findByEmail(email).orElseGet(WholesaleAccount::new);
        account.setUserId(-customerId);
        account.setUsername("shopify-customer-" + customerId);
        account.setEmail(email);
        account.setCompanyName(root.path("default_address").path("company").asText(null));
        account.setContactName(joinName(root.path("first_name").asText(""), root.path("last_name").asText("")));
        account.setPhone(root.path("phone").asText(null));
        account.setCountry(root.path("default_address").path("country_code").asText(null));
        account.setState(root.path("default_address").path("province_code").asText(null));

        if (suspendedByTag) {
            account.setStatus(WholesaleStatus.SUSPENDED);
            account.setCanViewPrice(false);
            account.setCanPlaceOrder(false);
        } else if (approved) {
            account.setStatus(WholesaleStatus.APPROVED);
            account.setCanViewPrice(true);
            account.setCanPlaceOrder(canOrder);
            if (account.getApprovedAt() == null) {
                account.setApprovedAt(LocalDateTime.now());
                account.setApprovedBy("shopify-tag-sync");
            }
        } else {
            account.setStatus(WholesaleStatus.PENDING_REVIEW);
            account.setCanViewPrice(false);
            account.setCanPlaceOrder(false);
        }
        wholesaleAccountRepository.save(account);
    }

    private void verifyHmac(String hmacHeader, String payload) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            throw new ForbiddenException("SHOPIFY_WEBHOOK_SECRET_MISSING", "Shopify webhook secret is not configured.");
        }
        if (hmacHeader == null || hmacHeader.isBlank()) {
            throw new ForbiddenException("SHOPIFY_WEBHOOK_SIGNATURE_MISSING", "Missing Shopify webhook signature.");
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = Base64.getEncoder().encodeToString(digest);
            if (!constantTimeEquals(computed, hmacHeader.trim())) {
                throw new ForbiddenException("SHOPIFY_WEBHOOK_SIGNATURE_INVALID", "Invalid Shopify webhook signature.");
            }
        } catch (ForbiddenException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ForbiddenException("SHOPIFY_WEBHOOK_VERIFICATION_FAILED", "Unable to verify Shopify webhook signature.");
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private BigDecimal parseAmount(String amount) {
        try {
            return new BigDecimal(amount).setScale(2, java.math.RoundingMode.HALF_UP);
        } catch (Exception ex) {
            return BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }

    private Set<String> parseTags(String rawTags) {
        Set<String> tags = new HashSet<>();
        if (rawTags == null || rawTags.isBlank()) {
            return tags;
        }
        String[] parts = rawTags.split(",");
        for (String part : parts) {
            if (part != null) {
                String value = part.trim();
                if (!value.isBlank()) {
                    tags.add(value.toUpperCase());
                }
            }
        }
        return tags;
    }

    private boolean extractBooleanMetafield(JsonNode root, String key) {
        JsonNode metafields = root.path("metafields");
        if (!metafields.isArray()) {
            return false;
        }
        for (JsonNode item : metafields) {
            String namespace = item.path("namespace").asText("");
            String k = item.path("key").asText("");
            String value = item.path("value").asText("");
            if ("wholesale".equalsIgnoreCase(namespace) && key.equalsIgnoreCase(k)) {
                return "true".equalsIgnoreCase(value) || "1".equals(value);
            }
        }
        return false;
    }

    private String joinName(String first, String last) {
        String fullName = (first + " " + last).trim();
        return fullName.isBlank() ? null : fullName;
    }
}
