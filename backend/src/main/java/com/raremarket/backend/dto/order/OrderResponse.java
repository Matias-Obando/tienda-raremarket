package com.raremarket.backend.dto.order;

import com.raremarket.backend.model.Order;

import java.time.OffsetDateTime;

public class OrderResponse {
    private String id;
    private String itemId;
    private String buyerId;
    private String sellerId;
    private String itemTitle;
    private String itemImage;
    private double amountEur;
    private String deliveryMethod;
    private String shippingFullName;
    private String shippingPhone;
    private String shippingAddressLine1;
    private String shippingCity;
    private String shippingPostalCode;
    private String shippingCountry;
    private String pickupCity;
    private String pickupNotes;
    private String paymentBrand;
    private String paymentLast4;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setItemId(order.getItemId());
        response.setBuyerId(order.getBuyerId());
        response.setSellerId(order.getSellerId());
        response.setItemTitle(order.getItemTitle());
        response.setItemImage(order.getItemImage());
        response.setAmountEur(order.getAmountEur());
        response.setDeliveryMethod(order.getDeliveryMethod());
        response.setShippingFullName(order.getShippingFullName());
        response.setShippingPhone(order.getShippingPhone());
        response.setShippingAddressLine1(order.getShippingAddressLine1());
        response.setShippingCity(order.getShippingCity());
        response.setShippingPostalCode(order.getShippingPostalCode());
        response.setShippingCountry(order.getShippingCountry());
        response.setPickupCity(order.getPickupCity());
        response.setPickupNotes(order.getPickupNotes());
        response.setPaymentBrand(order.getPaymentBrand());
        response.setPaymentLast4(order.getPaymentLast4());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
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
