package com.bazuuyu.b2b.wholesale.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class ConvertQuoteToOrderRequest {
    @NotNull
    private Long quoteId;
    @Email
    @NotNull
    private String buyerEmail;

    public Long getQuoteId() { return quoteId; }
    public void setQuoteId(Long quoteId) { this.quoteId = quoteId; }
    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
}
