package com.bazuuyu.b2b.wholesale.controller;

import com.bazuuyu.b2b.core.dto.ApiResponse;
import com.bazuuyu.b2b.wholesale.dto.request.PublicWholesaleApplicationRequest;
import com.bazuuyu.b2b.wholesale.dto.response.WholesaleApplicationResponse;
import com.bazuuyu.b2b.wholesale.dto.response.WholesalePricingAccessResponse;
import com.bazuuyu.b2b.wholesale.entity.WholesaleAccount;
import com.bazuuyu.b2b.wholesale.repository.WholesaleAccountRepository;
import com.bazuuyu.b2b.wholesale.service.WholesaleApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wholesale/public")
public class WholesaleStorefrontController {

    private final WholesaleApplicationService wholesaleApplicationService;
    private final WholesaleAccountRepository wholesaleAccountRepository;

    public WholesaleStorefrontController(
            WholesaleApplicationService wholesaleApplicationService,
            WholesaleAccountRepository wholesaleAccountRepository
    ) {
        this.wholesaleApplicationService = wholesaleApplicationService;
        this.wholesaleAccountRepository = wholesaleAccountRepository;
    }

    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<WholesaleApplicationResponse>> submitPublicApplication(
            @Valid @RequestBody PublicWholesaleApplicationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Wholesale application submitted.",
                wholesaleApplicationService.submitPublicApplication(request)
        ));
    }

    @GetMapping("/pricing-access")
    public ResponseEntity<ApiResponse<WholesalePricingAccessResponse>> getPricingAccessByEmail(
            @RequestParam String email
    ) {
        String normalizedEmail = email.trim().toLowerCase();
        WholesaleAccount account = wholesaleAccountRepository.findByEmail(normalizedEmail).orElse(null);

        WholesalePricingAccessResponse response = account == null
                ? new WholesalePricingAccessResponse(normalizedEmail, false, false, null)
                : new WholesalePricingAccessResponse(
                normalizedEmail,
                Boolean.TRUE.equals(account.getCanViewPrice()),
                Boolean.TRUE.equals(account.getCanPlaceOrder()),
                account.getStatus()
        );

        return ResponseEntity.ok(ApiResponse.success("Wholesale pricing access loaded.", response));
    }
}
