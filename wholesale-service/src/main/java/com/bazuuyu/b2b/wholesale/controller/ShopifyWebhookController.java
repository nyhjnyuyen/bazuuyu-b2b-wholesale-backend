package com.bazuuyu.b2b.wholesale.controller;

import com.bazuuyu.b2b.core.dto.ApiResponse;
import com.bazuuyu.b2b.wholesale.service.ShopifyWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wholesale/webhooks/shopify")
public class ShopifyWebhookController {

    private final ShopifyWebhookService shopifyWebhookService;

    public ShopifyWebhookController(ShopifyWebhookService shopifyWebhookService) {
        this.shopifyWebhookService = shopifyWebhookService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> receiveWebhook(
            @RequestHeader(name = "X-Shopify-Topic", required = false) String topic,
            @RequestHeader(name = "X-Shopify-Hmac-Sha256", required = false) String hmac,
            @RequestHeader(name = "X-Shopify-Webhook-Id", required = false) String webhookId,
            @RequestBody String payload
    ) {
        shopifyWebhookService.processWebhook(topic == null ? "" : topic.trim(), hmac, webhookId, payload);
        return ResponseEntity.ok(ApiResponse.success("Webhook processed.", null));
    }
}
