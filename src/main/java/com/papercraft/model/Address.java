package com.papercraft.model;

import java.io.Serializable;

public class Address implements Serializable {
    public int id;
    public Integer userId;
    public String fname;
    public String lname;
    public String nation;
    public String city;
    public String detailAddress;
    public String postcode;
    public String email;
    public String phone;
    public Boolean isDefault;

    public Address() {}

    public Address(int id, Integer userId, String fname, String lname, String nation, String city, String detailAddress, String postcode, String email, String phone, Boolean isDefault) {
        this.id = id;
        this.userId = userId;
        this.fname = fname;
        this.lname = lname;
        this.nation = nation;
        this.city = city;
        this.detailAddress = detailAddress;
        this.postcode = postcode;
        this.email = email;
        this.phone = phone;
        this.isDefault = isDefault;
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

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
