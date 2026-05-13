package com.bazuuyu.b2b.wholesale.controller;

import com.bazuuyu.b2b.core.dto.ApiResponse;
import com.bazuuyu.b2b.wholesale.dto.request.UpdateWholesaleAccountRequest;
import com.bazuuyu.b2b.wholesale.dto.response.WholesaleAccountResponse;
import com.bazuuyu.b2b.wholesale.service.WholesaleAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wholesale/accounts")
public class WholesaleAccountController {

    private final WholesaleAccountService wholesaleAccountService;

    public WholesaleAccountController(WholesaleAccountService wholesaleAccountService) {
        this.wholesaleAccountService = wholesaleAccountService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('WHOLESALE_BUYER')")
    public ResponseEntity<ApiResponse<WholesaleAccountResponse>> getMyAccount() {
        return ResponseEntity.ok(ApiResponse.success(
                "Current wholesale account loaded.",
                wholesaleAccountService.getMyAccount()
        ));
    }

    @GetMapping("/me/pricing-access")
    @PreAuthorize("hasRole('WHOLESALE_BUYER')")
    public ResponseEntity<ApiResponse<WholesaleAccountResponse>> getPricingAccess() {
        return ResponseEntity.ok(ApiResponse.success(
                "Wholesale pricing access confirmed.",
                wholesaleAccountService.getPricingAccess()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<WholesaleAccountResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Wholesale account loaded.",
                wholesaleAccountService.getById(id)
        ));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<WholesaleAccountResponse>> updateById(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWholesaleAccountRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Wholesale account updated.",
                wholesaleAccountService.updateById(id, request)
        ));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<List<WholesaleAccountResponse>>> getAllAccounts() {
        return ResponseEntity.ok(ApiResponse.success(
                "Wholesale accounts loaded.",
                wholesaleAccountService.getAllAccounts()
        ));
    }
}
