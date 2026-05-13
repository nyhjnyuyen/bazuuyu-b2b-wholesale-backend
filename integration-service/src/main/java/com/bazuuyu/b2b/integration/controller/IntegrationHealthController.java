package com.bazuuyu.b2b.integration.controller;

import com.bazuuyu.b2b.core.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationHealthController {

    @GetMapping("/capabilities")
    public ResponseEntity<ApiResponse<Map<String, Object>>> capabilities() {
        return ResponseEntity.ok(ApiResponse.success(
                "Integration service foundation is ready.",
                Map.of(
                        "shopify", "storefront/account/order integration surface",
                        "warehouse", "inventory sync and distributor operations",
                        "quotes", "manual quote and draft-order orchestration"
                )
        ));
    }
}
