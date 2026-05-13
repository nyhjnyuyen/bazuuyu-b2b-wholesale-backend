package com.bazuuyu.b2b.auth.controller;

import com.bazuuyu.b2b.core.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/access")
public class AccessController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> adminAccess() {
        return ResponseEntity.ok(ApiResponse.success("Admin access granted.", Map.of("role", "ADMIN")));
    }

    @GetMapping("/sales-manager")
    @PreAuthorize("hasRole('SALES_MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> salesManagerAccess() {
        return ResponseEntity.ok(ApiResponse.success("Sales manager access granted.", Map.of("role", "SALES_MANAGER")));
    }

    @GetMapping("/buyer")
    @PreAuthorize("hasRole('WHOLESALE_BUYER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> buyerAccess() {
        return ResponseEntity.ok(ApiResponse.success("Wholesale buyer access granted.", Map.of("role", "WHOLESALE_BUYER")));
    }
}
