package com.bazuuyu.b2b.wholesale.service;

import com.bazuuyu.b2b.core.exception.ForbiddenException;
import com.bazuuyu.b2b.core.exception.NotFoundException;
import com.bazuuyu.b2b.core.security.AuthenticatedUser;
import com.bazuuyu.b2b.core.security.SecurityUtils;
import com.bazuuyu.b2b.wholesale.dto.request.UpdateWholesaleAccountRequest;
import com.bazuuyu.b2b.wholesale.dto.response.WholesaleAccountResponse;
import com.bazuuyu.b2b.wholesale.entity.WholesaleAccount;
import com.bazuuyu.b2b.wholesale.entity.enums.WholesaleStatus;
import com.bazuuyu.b2b.wholesale.repository.WholesaleAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WholesaleAccountService {

    private final WholesaleAccountRepository wholesaleAccountRepository;

    public WholesaleAccountService(WholesaleAccountRepository wholesaleAccountRepository) {
        this.wholesaleAccountRepository = wholesaleAccountRepository;
    }

    public WholesaleAccountResponse getMyAccount() {
        AuthenticatedUser currentUser = SecurityUtils.getAuthenticatedUser();
        WholesaleAccount account = wholesaleAccountRepository.findByUserId(currentUser.userId())
                .orElseThrow(() -> new NotFoundException(
                        "WHOLESALE_ACCOUNT_NOT_FOUND",
                        "Wholesale account not found for current user."
                ));
        return toResponse(account);
    }

    public WholesaleAccountResponse getById(Long id) {
        WholesaleAccount account = wholesaleAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "WHOLESALE_ACCOUNT_NOT_FOUND",
                        "Wholesale account not found."
                ));
        return toResponse(account);
    }

    public List<WholesaleAccountResponse> getAllAccounts() {
        return wholesaleAccountRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public WholesaleAccountResponse updateById(Long id, UpdateWholesaleAccountRequest request) {
        WholesaleAccount account = wholesaleAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "WHOLESALE_ACCOUNT_NOT_FOUND",
                        "Wholesale account not found."
                ));

        if (request.getCompanyName() != null) {
            account.setCompanyName(request.getCompanyName());
        }
        if (request.getContactName() != null) {
            account.setContactName(request.getContactName());
        }
        if (request.getPhone() != null) {
            account.setPhone(request.getPhone());
        }
        if (request.getBusinessType() != null) {
            account.setBusinessType(request.getBusinessType());
        }
        if (request.getCountry() != null) {
            account.setCountry(request.getCountry());
        }
        if (request.getState() != null) {
            account.setState(request.getState());
        }
        if (request.getStatus() != null) {
            applyStatus(account, request.getStatus());
        }

        return toResponse(wholesaleAccountRepository.save(account));
    }

    public WholesaleAccountResponse getPricingAccess() {
        WholesaleAccountResponse response = getMyAccount();
        if (!Boolean.TRUE.equals(response.getCanViewPrice())) {
            throw new ForbiddenException(
                    "WHOLESALE_PRICING_LOCKED",
                    "Wholesale pricing is only available to approved buyers."
            );
        }
        return response;
    }

    private void applyStatus(WholesaleAccount account, WholesaleStatus status) {
        account.setStatus(status);
        if (status == WholesaleStatus.APPROVED) {
            account.setCanViewPrice(true);
            account.setCanPlaceOrder(true);
            account.setApprovedAt(LocalDateTime.now());
            account.setApprovedBy(SecurityUtils.currentUsername());
            return;
        }

        account.setCanViewPrice(false);
        account.setCanPlaceOrder(false);
    }

    private WholesaleAccountResponse toResponse(WholesaleAccount account) {
        WholesaleAccountResponse response = new WholesaleAccountResponse();
        response.setId(account.getId());
        response.setUserId(account.getUserId());
        response.setUsername(account.getUsername());
        response.setEmail(account.getEmail());
        response.setCompanyName(account.getCompanyName());
        response.setContactName(account.getContactName());
        response.setPhone(account.getPhone());
        response.setBusinessType(account.getBusinessType());
        response.setCountry(account.getCountry());
        response.setState(account.getState());
        response.setStatus(account.getStatus());
        response.setCanViewPrice(account.getCanViewPrice());
        response.setCanPlaceOrder(account.getCanPlaceOrder());
        response.setApprovedAt(account.getApprovedAt());
        response.setApprovedBy(account.getApprovedBy());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        return response;
    }
}
