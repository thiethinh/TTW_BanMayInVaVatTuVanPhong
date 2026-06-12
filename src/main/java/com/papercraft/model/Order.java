package com.papercraft.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    public int id;
    public Integer userId;
    public String status;
    public BigDecimal totalPrice;
    public String note;
    public BigDecimal shippingFee;
    private String shippingProvider;
    public String shippingName;
    public String shippingPhone;
    public String shippingAddress;
    public Timestamp createdAt;
    public List<OrderItem> orderItems;

    private String ghnOrderCode;
    private String ghnStatus;

    private Integer shippingProvinceId;
    private String shippingProvinceName;
    private Integer shippingDistrictId;
    private String shippingDistrictName;
    private String shippingWardCode;
    private String shippingWardName;


    public Order() {
    }

    public Order(int id, Integer userId, String status, BigDecimal totalPrice, String note, BigDecimal shippingFee, String shippingProvider,
                 String shippingName, String shippingPhone, String shippingAddress, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.note = note;
        this.shippingFee = shippingFee;
        this.shippingProvider = shippingProvider;
        this.shippingName = shippingName;
        this.shippingPhone = shippingPhone;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.orderItems = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getShippingProvider() {
        return shippingProvider;
    }

    public void setShippingProvider(String shippingProvider) {
        this.shippingProvider = shippingProvider;
    }

    public String getGhnOrderCode() {return ghnOrderCode;}

    public void setGhnOrderCode(String ghnOrderCode) {this.ghnOrderCode = ghnOrderCode;}

    public String getGhnStatus() {return ghnStatus;}

    public void setGhnStatus(String ghnStatus) {this.ghnStatus = ghnStatus;}

    public Integer getShippingProvinceId() {return shippingProvinceId;}

    public void setShippingProvinceId(Integer shippingProvinceId) {this.shippingProvinceId = shippingProvinceId;}

    public String getShippingProvinceName() {return shippingProvinceName;}

    public void setShippingProvinceName(String shippingProvinceName) {this.shippingProvinceName = shippingProvinceName;}

    public Integer getShippingDistrictId() {return shippingDistrictId;}

    public void setShippingDistrictId(Integer shippingDistrictId) {this.shippingDistrictId = shippingDistrictId;}

    public String getShippingDistrictName() {return shippingDistrictName;}

    public void setShippingDistrictName(String shippingDistrictName) {this.shippingDistrictName = shippingDistrictName;}

    public String getShippingWardCode() {return shippingWardCode;}

    public void setShippingWardCode(String shippingWardCode) {this.shippingWardCode = shippingWardCode;}

    public String getShippingWardName() {return shippingWardName;}

    public void setShippingWardName(String shippingWardName) {this.shippingWardName = shippingWardName;}
}
