package com.bazuuyu.b2b.wholesale.dto.request;

import com.bazuuyu.b2b.wholesale.entity.enums.ShipmentMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpsertInventoryRequest {
    @NotBlank
    private String sku;
    @NotNull
    @Min(0)
    private Integer availableQuantity;
    @NotNull
    private ShipmentMode sourceMode;
    @NotNull
    @Min(0)
    private Integer etaDays;

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }
    public ShipmentMode getSourceMode() { return sourceMode; }
    public void setSourceMode(ShipmentMode sourceMode) { this.sourceMode = sourceMode; }
    public Integer getEtaDays() { return etaDays; }
    public void setEtaDays(Integer etaDays) { this.etaDays = etaDays; }
}
