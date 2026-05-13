package com.bazuuyu.b2b.wholesale.entity;

import com.bazuuyu.b2b.wholesale.entity.enums.PriceChannel;
import com.bazuuyu.b2b.wholesale.entity.enums.ShipmentMode;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wholesale_quotes")
public class WholesaleQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String buyerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PriceChannel priceChannel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShipmentMode shipmentMode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingFee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private Boolean convertedToOrder;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (convertedToOrder == null) convertedToOrder = false;
    }

    public Long getId() { return id; }
    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
    public PriceChannel getPriceChannel() { return priceChannel; }
    public void setPriceChannel(PriceChannel priceChannel) { this.priceChannel = priceChannel; }
    public ShipmentMode getShipmentMode() { return shipmentMode; }
    public void setShipmentMode(ShipmentMode shipmentMode) { this.shipmentMode = shipmentMode; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public Boolean getConvertedToOrder() { return convertedToOrder; }
    public void setConvertedToOrder(Boolean convertedToOrder) { this.convertedToOrder = convertedToOrder; }
}
