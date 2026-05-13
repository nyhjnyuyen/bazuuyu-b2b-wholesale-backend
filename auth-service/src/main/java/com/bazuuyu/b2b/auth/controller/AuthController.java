package com.bazuuyu.b2b.auth.controller;

import com.bazuuyu.b2b.auth.dto.LoginRequest;
import com.bazuuyu.b2b.auth.dto.RegisterRequest;
import com.bazuuyu.b2b.auth.service.AuthService;
import com.bazuuyu.b2b.core.dto.ApiResponse;
import com.bazuuyu.b2b.core.dto.RegisterResponse;
import com.bazuuyu.b2b.core.dto.TokenResponse;
import com.bazuuyu.b2b.core.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Registration completed successfully.",
                authService.register(request)
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Login successful.",
                authService.login(request)
        ));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TokenResponse>> me() {
        return ResponseEntity.ok(ApiResponse.success(
                "Current user profile loaded.",
                authService.getCurrentUserProfile(SecurityUtils.currentUsername())
        ));
    }
}
