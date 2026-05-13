package com.bazuuyu.b2b.wholesale.entity;

import com.bazuuyu.b2b.wholesale.entity.enums.ApplicationReviewStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wholesale_application_records")
public class WholesaleApplicationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 200)
    private String companyName;

    @Column(nullable = false, length = 100)
    private String contactName;

    @Column(length = 50)
    private String phone;

    @Column(length = 100)
    private String businessType;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String state;

    @Column(length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApplicationReviewStatus reviewStatus;

    @Column(length = 1000)
    private String reviewNote;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    @Column(length = 100)
    private String reviewedBy;

    @PrePersist
    public void prePersist() {
        if (this.reviewStatus == null) {
            this.reviewStatus = ApplicationReviewStatus.SUBMITTED;
        }
        if (this.submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }
    }

    public WholesaleApplicationRecord() {
    }

    public Long getId() {
        return id;
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
}
