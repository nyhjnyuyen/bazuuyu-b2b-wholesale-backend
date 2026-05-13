package com.bazuuyu.b2b.wholesale.service;

import com.bazuuyu.b2b.core.exception.ConflictException;
import com.bazuuyu.b2b.core.exception.ForbiddenException;
import com.bazuuyu.b2b.core.exception.NotFoundException;
import com.bazuuyu.b2b.core.security.AuthenticatedUser;
import com.bazuuyu.b2b.core.security.SecurityUtils;
import com.bazuuyu.b2b.wholesale.dto.request.ReviewWholesaleApplicationRequest;
import com.bazuuyu.b2b.wholesale.dto.request.SubmitWholesaleApplicationRequest;
import com.bazuuyu.b2b.wholesale.dto.request.PublicWholesaleApplicationRequest;
import com.bazuuyu.b2b.wholesale.dto.response.WholesaleApplicationResponse;
import com.bazuuyu.b2b.wholesale.entity.WholesaleAccount;
import com.bazuuyu.b2b.wholesale.entity.WholesaleApplicationRecord;
import com.bazuuyu.b2b.wholesale.entity.FunnelEvent;
import com.bazuuyu.b2b.wholesale.entity.enums.ApplicationReviewStatus;
import com.bazuuyu.b2b.wholesale.entity.enums.FunnelEventType;
import com.bazuuyu.b2b.wholesale.entity.enums.WholesaleStatus;
import com.bazuuyu.b2b.wholesale.repository.WholesaleAccountRepository;
import com.bazuuyu.b2b.wholesale.repository.WholesaleApplicationRecordRepository;
import com.bazuuyu.b2b.wholesale.repository.FunnelEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WholesaleApplicationService {

    private final WholesaleAccountRepository wholesaleAccountRepository;
    private final WholesaleApplicationRecordRepository wholesaleApplicationRecordRepository;
    private final FunnelEventRepository funnelEventRepository;

    public WholesaleApplicationService(
            WholesaleAccountRepository wholesaleAccountRepository,
            WholesaleApplicationRecordRepository wholesaleApplicationRecordRepository,
            FunnelEventRepository funnelEventRepository
    ) {
        this.wholesaleAccountRepository = wholesaleAccountRepository;
        this.wholesaleApplicationRecordRepository = wholesaleApplicationRecordRepository;
        this.funnelEventRepository = funnelEventRepository;
    }

    @Transactional
    public WholesaleApplicationResponse submitApplication(SubmitWholesaleApplicationRequest request) {
        AuthenticatedUser currentUser = SecurityUtils.getAuthenticatedUser();

        if (wholesaleApplicationRecordRepository.existsByUserIdAndReviewStatusIn(
                currentUser.userId(),
                List.of(ApplicationReviewStatus.SUBMITTED, ApplicationReviewStatus.UNDER_REVIEW)
        )) {
            throw new ConflictException(
                    "WHOLESALE_APPLICATION_EXISTS",
                    "A wholesale application is already pending review for this buyer."
            );
        }

        WholesaleApplicationRecord application = new WholesaleApplicationRecord();
        application.setUserId(currentUser.userId());
        application.setUsername(currentUser.username());
        application.setEmail(currentUser.email());
        application.setCompanyName(request.getCompanyName());
        application.setContactName(request.getContactName());
        application.setPhone(request.getPhone());
        application.setBusinessType(request.getBusinessType());
        application.setCountry(request.getCountry());
        application.setState(request.getState());
        application.setNote(request.getNote());
        application.setReviewStatus(ApplicationReviewStatus.SUBMITTED);

        WholesaleApplicationRecord savedApplication = wholesaleApplicationRecordRepository.save(application);

        WholesaleAccount account = wholesaleAccountRepository.findByUserId(currentUser.userId())
                .orElseGet(WholesaleAccount::new);
        account.setUserId(currentUser.userId());
        account.setUsername(currentUser.username());
        account.setEmail(currentUser.email());
        account.setCompanyName(request.getCompanyName());
        account.setContactName(request.getContactName());
        account.setPhone(request.getPhone());
        account.setBusinessType(request.getBusinessType());
        account.setCountry(request.getCountry());
        account.setState(request.getState());
        account.setStatus(WholesaleStatus.PENDING_REVIEW);
        account.setCanViewPrice(false);
        account.setCanPlaceOrder(false);

        WholesaleAccount savedAccount = wholesaleAccountRepository.save(account);
        trackEvent(currentUser.email(), FunnelEventType.REGISTRATION_SUBMITTED, "buyer-portal");
        return toResponse(savedApplication, savedAccount);
    }

    @Transactional
    public WholesaleApplicationResponse reviewApplication(Long applicationId, ReviewWholesaleApplicationRequest request) {
        WholesaleApplicationRecord application = wholesaleApplicationRecordRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException(
                        "WHOLESALE_APPLICATION_NOT_FOUND",
                        "Wholesale application not found."
                ));

        if (request.getDecision() != ApplicationReviewStatus.APPROVED
                && request.getDecision() != ApplicationReviewStatus.REJECTED) {
            throw new ForbiddenException(
                    "INVALID_REVIEW_DECISION",
                    "Application decision must be APPROVED or REJECTED."
            );
        }

        application.setReviewStatus(request.getDecision());
        application.setReviewNote(request.getReviewNote());
        application.setReviewedBy(SecurityUtils.currentUsername());
        application.setReviewedAt(LocalDateTime.now());

        WholesaleAccount account = wholesaleAccountRepository.findByUserId(application.getUserId())
                .orElseGet(WholesaleAccount::new);
        account.setUserId(application.getUserId());
        account.setUsername(application.getUsername());
        account.setEmail(application.getEmail());
        account.setCompanyName(application.getCompanyName());
        account.setContactName(application.getContactName());
        account.setPhone(application.getPhone());
        account.setBusinessType(application.getBusinessType());
        account.setCountry(application.getCountry());
        account.setState(application.getState());

        if (request.getDecision() == ApplicationReviewStatus.APPROVED) {
            account.setStatus(WholesaleStatus.APPROVED);
            account.setCanViewPrice(true);
            account.setCanPlaceOrder(true);
            account.setApprovedAt(LocalDateTime.now());
            account.setApprovedBy(SecurityUtils.currentUsername());
        } else {
            account.setStatus(WholesaleStatus.REJECTED);
            account.setCanViewPrice(false);
            account.setCanPlaceOrder(false);
        }

        WholesaleApplicationRecord savedApplication = wholesaleApplicationRecordRepository.save(application);
        WholesaleAccount savedAccount = wholesaleAccountRepository.save(account);
        if (request.getDecision() == ApplicationReviewStatus.APPROVED) {
            trackEvent(application.getEmail(), FunnelEventType.REGISTRATION_APPROVED, "review");
        }
        return toResponse(savedApplication, savedAccount);
    }

    public WholesaleApplicationResponse getCurrentApplication() {
        AuthenticatedUser currentUser = SecurityUtils.getAuthenticatedUser();
        WholesaleApplicationRecord application = wholesaleApplicationRecordRepository
                .findTopByUserIdOrderBySubmittedAtDesc(currentUser.userId())
                .orElseThrow(() -> new NotFoundException(
                        "WHOLESALE_APPLICATION_NOT_FOUND",
                        "No wholesale application found for the current buyer."
                ));
        WholesaleAccount account = wholesaleAccountRepository.findByUserId(currentUser.userId()).orElse(null);
        return toResponse(application, account);
    }

    public WholesaleApplicationResponse getApplicationById(Long applicationId) {
        WholesaleApplicationRecord application = wholesaleApplicationRecordRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException(
                        "WHOLESALE_APPLICATION_NOT_FOUND",
                        "Wholesale application not found."
                ));
        WholesaleAccount account = wholesaleAccountRepository.findByUserId(application.getUserId()).orElse(null);
        return toResponse(application, account);
    }

    public List<WholesaleApplicationResponse> getPendingApplications() {
        return wholesaleApplicationRecordRepository.findByReviewStatus(ApplicationReviewStatus.SUBMITTED)
                .stream()
                .map(application -> toResponse(
                        application,
                        wholesaleAccountRepository.findByUserId(application.getUserId()).orElse(null)
                ))
                .toList();
    }

    @Transactional
    public WholesaleApplicationResponse submitPublicApplication(PublicWholesaleApplicationRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (wholesaleApplicationRecordRepository.existsByEmailAndReviewStatusIn(
                normalizedEmail,
                List.of(ApplicationReviewStatus.SUBMITTED, ApplicationReviewStatus.UNDER_REVIEW)
        )) {
            throw new ConflictException(
                    "WHOLESALE_APPLICATION_EXISTS",
                    "A wholesale application is already pending review for this email."
            );
        }

        WholesaleApplicationRecord application = new WholesaleApplicationRecord();
        application.setUserId(generateProspectUserId(normalizedEmail));
        application.setUsername(buildProspectUsername(normalizedEmail));
        application.setEmail(normalizedEmail);
        application.setCompanyName(request.getCompanyName());
        application.setContactName(request.getContactName());
        application.setPhone(request.getPhone());
        application.setBusinessType(request.getBusinessType());
        application.setCountry(request.getCountry());
        application.setState(request.getState());
        application.setNote(buildPublicApplicationNote(request));
        application.setReviewStatus(ApplicationReviewStatus.SUBMITTED);

        WholesaleApplicationRecord savedApplication = wholesaleApplicationRecordRepository.save(application);

        WholesaleAccount account = wholesaleAccountRepository.findByEmail(normalizedEmail)
                .orElseGet(WholesaleAccount::new);
        account.setUserId(savedApplication.getUserId());
        account.setUsername(savedApplication.getUsername());
        account.setEmail(normalizedEmail);
        account.setCompanyName(request.getCompanyName());
        account.setContactName(request.getContactName());
        account.setPhone(request.getPhone());
        account.setBusinessType(request.getBusinessType());
        account.setCountry(request.getCountry());
        account.setState(request.getState());
        account.setStatus(WholesaleStatus.PENDING_REVIEW);
        account.setCanViewPrice(false);
        account.setCanPlaceOrder(false);

        WholesaleAccount savedAccount = wholesaleAccountRepository.save(account);
        trackEvent(normalizedEmail, FunnelEventType.REGISTRATION_SUBMITTED, "shopify-signup");
        return toResponse(savedApplication, savedAccount);
    }

    private void trackEvent(String email, FunnelEventType eventType, String source) {
        FunnelEvent event = new FunnelEvent();
        event.setEmail(email);
        event.setEventType(eventType);
        event.setSource(source);
        funnelEventRepository.save(event);
    }

    private Long generateProspectUserId(String email) {
        long base = Math.abs(email.hashCode());
        if (base == 0) {
            base = System.currentTimeMillis();
        }
        long candidate = -base;
        while (wholesaleAccountRepository.findByUserId(candidate).isPresent()) {
            candidate -= 1L;
        }
        return candidate;
    }

    private String buildProspectUsername(String email) {
        int atIndex = email.indexOf('@');
        String prefix = atIndex > 0 ? email.substring(0, atIndex) : email;
        if (prefix.length() > 80) {
            prefix = prefix.substring(0, 80);
        }
        return "prospect-" + prefix;
    }

    private String buildPublicApplicationNote(PublicWholesaleApplicationRequest request) {
        StringBuilder noteBuilder = new StringBuilder();
        noteBuilder.append("[Shopify signup]");
        if (request.getWebsiteOrSocial() != null && !request.getWebsiteOrSocial().isBlank()) {
            noteBuilder.append(" website/social=").append(request.getWebsiteOrSocial().trim());
        }
        if (request.getInterestedProducts() != null && !request.getInterestedProducts().isBlank()) {
            noteBuilder.append(" interestedProducts=").append(request.getInterestedProducts().trim());
        }
        if (request.getNote() != null && !request.getNote().isBlank()) {
            noteBuilder.append(" note=").append(request.getNote().trim());
        }

        String note = noteBuilder.toString();
        if (note.length() > 1000) {
            return note.substring(0, 1000);
        }
        return note;
    }

    private WholesaleApplicationResponse toResponse(WholesaleApplicationRecord application, WholesaleAccount account) {
        WholesaleApplicationResponse response = new WholesaleApplicationResponse();
        response.setApplicationId(application.getId());
        response.setUserId(application.getUserId());
        response.setUsername(application.getUsername());
        response.setEmail(application.getEmail());
        response.setCompanyName(application.getCompanyName());
        response.setContactName(application.getContactName());
        response.setPhone(application.getPhone());
        response.setBusinessType(application.getBusinessType());
        response.setCountry(application.getCountry());
        response.setState(application.getState());
        response.setNote(application.getNote());
        response.setReviewStatus(application.getReviewStatus());
        response.setReviewNote(application.getReviewNote());
        response.setSubmittedAt(application.getSubmittedAt());
        response.setReviewedAt(application.getReviewedAt());
        response.setReviewedBy(application.getReviewedBy());
        response.setWholesaleStatus(account != null ? account.getStatus() : null);
        return response;
    }
}
