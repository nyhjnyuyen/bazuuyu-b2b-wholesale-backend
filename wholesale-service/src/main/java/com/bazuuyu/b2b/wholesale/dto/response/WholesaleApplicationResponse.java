package com.bazuuyu.b2b.wholesale.dto.response;

import com.bazuuyu.b2b.wholesale.entity.enums.ApplicationReviewStatus;
import com.bazuuyu.b2b.wholesale.entity.enums.WholesaleStatus;

import java.time.LocalDateTime;

public class WholesaleApplicationResponse {

    private Long applicationId;
    private Long userId;
    private String username;
    private String email;
    private String companyName;
    private String contactName;
    private String phone;
    private String businessType;
    private String country;
    private String state;
    private String note;
    private ApplicationReviewStatus reviewStatus;
    private String reviewNote;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private WholesaleStatus wholesaleStatus;

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ApplicationReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ApplicationReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public WholesaleStatus getWholesaleStatus() {
        return wholesaleStatus;
    }

    public void setWholesaleStatus(WholesaleStatus wholesaleStatus) {
        this.wholesaleStatus = wholesaleStatus;
    }
}
