package com.bazuuyu.b2b.wholesale.dto.request;

import com.bazuuyu.b2b.wholesale.entity.enums.ShipmentMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpsertShippingPolicyRequest {
    @NotNull
    private ShipmentMode mode;
    @NotNull
    @Min(0)
    private BigDecimal freeShippingThreshold;
    @NotNull
    @Min(0)
    private BigDecimal flatShippingFee;
    @NotNull
    @Min(0)
    private BigDecimal perKgFee;

    public ShipmentMode getMode() { return mode; }
    public void setMode(ShipmentMode mode) { this.mode = mode; }
    public BigDecimal getFreeShippingThreshold() { return freeShippingThreshold; }
    public void setFreeShippingThreshold(BigDecimal freeShippingThreshold) { this.freeShippingThreshold = freeShippingThreshold; }
    public BigDecimal getFlatShippingFee() { return flatShippingFee; }
    public void setFlatShippingFee(BigDecimal flatShippingFee) { this.flatShippingFee = flatShippingFee; }
    public BigDecimal getPerKgFee() { return perKgFee; }
    public void setPerKgFee(BigDecimal perKgFee) { this.perKgFee = perKgFee; }
}
