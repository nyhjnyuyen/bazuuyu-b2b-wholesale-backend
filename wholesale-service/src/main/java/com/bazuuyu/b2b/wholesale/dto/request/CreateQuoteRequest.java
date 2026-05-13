package com.bazuuyu.b2b.wholesale.dto.request;

import com.bazuuyu.b2b.wholesale.entity.enums.PriceChannel;
import com.bazuuyu.b2b.wholesale.entity.enums.ShipmentMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateQuoteRequest {
    @Email
    @NotNull
    private String buyerEmail;
    @NotNull
    private PriceChannel priceChannel;
    @NotNull
    private ShipmentMode shipmentMode;
    @Valid
    @NotEmpty
    private List<QuoteLineRequest> items;

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
    public PriceChannel getPriceChannel() { return priceChannel; }
    public void setPriceChannel(PriceChannel priceChannel) { this.priceChannel = priceChannel; }
    public ShipmentMode getShipmentMode() { return shipmentMode; }
    public void setShipmentMode(ShipmentMode shipmentMode) { this.shipmentMode = shipmentMode; }
    public List<QuoteLineRequest> getItems() { return items; }
    public void setItems(List<QuoteLineRequest> items) { this.items = items; }
}
