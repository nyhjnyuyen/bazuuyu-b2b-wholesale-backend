package com.bazuuyu.b2b.wholesale.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PublicWholesaleApplicationRequest {

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String contactName;

    @NotBlank
    @Size(max = 200)
    private String companyName;

    @Size(max = 100)
    private String websiteOrSocial;

    @Size(max = 50)
    private String phone;

    @Size(max = 100)
    private String businessType;

    @Size(max = 100)
    private String country;

    @Size(max = 100)
    private String state;

    @Size(max = 1000)
    private String interestedProducts;

    @Size(max = 1000)
    private String note;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWebsiteOrSocial() {
        return websiteOrSocial;
    }

    public void setWebsiteOrSocial(String websiteOrSocial) {
        this.websiteOrSocial = websiteOrSocial;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInterestedProducts() {
        return interestedProducts;
    }

    public void setInterestedProducts(String interestedProducts) {
        this.interestedProducts = interestedProducts;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
