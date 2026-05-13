package com.bazuuyu.b2b.wholesale.entity;

import com.bazuuyu.b2b.wholesale.entity.enums.WholesaleStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wholesale_accounts")
public class WholesaleAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 200)
    private String companyName;

    @Column(length = 100)
    private String contactName;

    @Column(length = 50)
    private String phone;

    @Column(length = 100)
    private String businessType;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WholesaleStatus status;

    @Column(nullable = false)
    private Boolean canViewPrice;

    @Column(nullable = false)
    private Boolean canPlaceOrder;

    private LocalDateTime approvedAt;

    @Column(length = 100)
    private String approvedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = WholesaleStatus.PENDING_REVIEW;
        }
        if (this.canViewPrice == null) {
            this.canViewPrice = false;
        }
        if (this.canPlaceOrder == null) {
            this.canPlaceOrder = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public WholesaleAccount() {
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

    public WholesaleStatus getStatus() {
        return status;
    }

    public void setStatus(WholesaleStatus status) {
        this.status = status;
    }

    public Boolean getCanViewPrice() {
        return canViewPrice;
    }

    public void setCanViewPrice(Boolean canViewPrice) {
        this.canViewPrice = canViewPrice;
    }

    public Boolean getCanPlaceOrder() {
        return canPlaceOrder;
    }

    public void setCanPlaceOrder(Boolean canPlaceOrder) {
        this.canPlaceOrder = canPlaceOrder;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
