package com.raremarket.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "item_id", nullable = false, length = 80)
    private String itemId;

    @Column(name = "buyer_id", nullable = false, length = 36)
    private String buyerId;

    @Column(name = "seller_id", nullable = false, length = 36)
    private String sellerId;

    @Column(name = "item_title", nullable = false, length = 180)
    private String itemTitle;

    @Column(name = "item_image", nullable = false, columnDefinition = "text")
    private String itemImage;

    @Column(name = "amount_eur", nullable = false)
    private double amountEur;

    @Column(name = "delivery_method", nullable = false, length = 20)
    private String deliveryMethod;

    @Column(name = "shipping_full_name", length = 120)
    private String shippingFullName;

    @Column(name = "shipping_phone", length = 40)
    private String shippingPhone;

    @Column(name = "shipping_address_line1", length = 220)
    private String shippingAddressLine1;

    @Column(name = "shipping_city", length = 120)
    private String shippingCity;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    @Column(name = "shipping_country", length = 80)
    private String shippingCountry;

    @Column(name = "pickup_city", length = 120)
    private String pickupCity;

    @Column(name = "pickup_notes", length = 500)
    private String pickupNotes;

    @Column(name = "payment_brand", nullable = false, length = 30)
    private String paymentBrand;

    @Column(name = "payment_last4", nullable = false, length = 4)
    private String paymentLast4;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public double getAmountEur() {
        return amountEur;
    }

    public void setAmountEur(double amountEur) {
        this.amountEur = amountEur;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getShippingFullName() {
        return shippingFullName;
    }

    public void setShippingFullName(String shippingFullName) {
        this.shippingFullName = shippingFullName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public String getShippingAddressLine1() {
        return shippingAddressLine1;
    }

    public void setShippingAddressLine1(String shippingAddressLine1) {
        this.shippingAddressLine1 = shippingAddressLine1;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }

    public String getShippingCountry() {
        return shippingCountry;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    public String getPickupCity() {
        return pickupCity;
    }

    public void setPickupCity(String pickupCity) {
        this.pickupCity = pickupCity;
    }

    public String getPickupNotes() {
        return pickupNotes;
    }

    public void setPickupNotes(String pickupNotes) {
        this.pickupNotes = pickupNotes;
    }

    public String getPaymentBrand() {
        return paymentBrand;
    }

    public void setPaymentBrand(String paymentBrand) {
        this.paymentBrand = paymentBrand;
    }

    public String getPaymentLast4() {
        return paymentLast4;
    }

    public void setPaymentLast4(String paymentLast4) {
        this.paymentLast4 = paymentLast4;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
