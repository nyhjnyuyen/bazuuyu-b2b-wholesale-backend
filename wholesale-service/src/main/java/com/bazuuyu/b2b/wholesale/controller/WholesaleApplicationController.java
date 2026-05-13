package com.bazuuyu.b2b.wholesale.controller;

import com.bazuuyu.b2b.core.dto.ApiResponse;
import com.bazuuyu.b2b.wholesale.dto.request.ReviewWholesaleApplicationRequest;
import com.bazuuyu.b2b.wholesale.dto.request.SubmitWholesaleApplicationRequest;
import com.bazuuyu.b2b.wholesale.dto.response.WholesaleApplicationResponse;
import com.bazuuyu.b2b.wholesale.service.WholesaleApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wholesale/applications")
public class WholesaleApplicationController {

    private final WholesaleApplicationService wholesaleApplicationService;

    public WholesaleApplicationController(WholesaleApplicationService wholesaleApplicationService) {
        this.wholesaleApplicationService = wholesaleApplicationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('WHOLESALE_BUYER')")
    public ResponseEntity<ApiResponse<WholesaleApplicationResponse>> submitApplication(
            @Valid @RequestBody SubmitWholesaleApplicationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Wholesale application submitted.",
                wholesaleApplicationService.submitApplication(request)
        ));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<WholesaleApplicationResponse>> reviewApplication(
            @PathVariable Long id,
            @Valid @RequestBody ReviewWholesaleApplicationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Wholesale application reviewed.",
                wholesaleApplicationService.reviewApplication(id, request)
        ));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('WHOLESALE_BUYER')")
    public ResponseEntity<ApiResponse<WholesaleApplicationResponse>> getCurrentApplication() {
        return ResponseEntity.ok(ApiResponse.success(
                "Current wholesale application loaded.",
                wholesaleApplicationService.getCurrentApplication()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<WholesaleApplicationResponse>> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Wholesale application loaded.",
                wholesaleApplicationService.getApplicationById(id)
        ));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<ApiResponse<List<WholesaleApplicationResponse>>> getPendingApplications() {
        return ResponseEntity.ok(ApiResponse.success(
                "Pending wholesale applications loaded.",
                wholesaleApplicationService.getPendingApplications()
        ));
    }
}
