package com.bazuuyu.b2b.wholesale.dto.request;

import com.bazuuyu.b2b.wholesale.entity.enums.PriceChannel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpsertPricingRuleRequest {
    @NotBlank
    private String sku;
    @NotNull
    private PriceChannel channel;
    @NotNull
    @Min(1)
    private Integer minQuantity;
    @NotNull
    @Min(0)
    private BigDecimal unitPrice;

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public PriceChannel getChannel() { return channel; }
    public void setChannel(PriceChannel channel) { this.channel = channel; }
    public Integer getMinQuantity() { return minQuantity; }
    public void setMinQuantity(Integer minQuantity) { this.minQuantity = minQuantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
