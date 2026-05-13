package com.bazuuyu.b2b.wholesale.entity;

import com.bazuuyu.b2b.wholesale.entity.enums.ShipmentMode;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_policies")
public class ShippingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShipmentMode mode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal freeShippingThreshold;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal flatShippingFee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal perKgFee;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (active == null) active = true;
    }

    @PreUpdate
    public void preUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public ShipmentMode getMode() { return mode; }
    public void setMode(ShipmentMode mode) { this.mode = mode; }
    public BigDecimal getFreeShippingThreshold() { return freeShippingThreshold; }
    public void setFreeShippingThreshold(BigDecimal freeShippingThreshold) { this.freeShippingThreshold = freeShippingThreshold; }
    public BigDecimal getFlatShippingFee() { return flatShippingFee; }
    public void setFlatShippingFee(BigDecimal flatShippingFee) { this.flatShippingFee = flatShippingFee; }
    public BigDecimal getPerKgFee() { return perKgFee; }
    public void setPerKgFee(BigDecimal perKgFee) { this.perKgFee = perKgFee; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
