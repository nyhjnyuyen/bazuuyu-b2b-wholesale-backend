package com.bazuuyu.b2b.wholesale.dto.request;

import com.bazuuyu.b2b.wholesale.entity.enums.WholesaleStatus;
import jakarta.validation.constraints.Size;

public class UpdateWholesaleAccountRequest {

    @Size(max = 200)
    private String companyName;

    @Size(max = 100)
    private String contactName;

    @Size(max = 50)
    private String phone;

    @Size(max = 100)
    private String businessType;

    @Size(max = 100)
    private String country;

    @Size(max = 100)
    private String state;

    private WholesaleStatus status;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public WholesaleStatus getStatus() {
        return status;
    }

    public void setStatus(WholesaleStatus status) {
        this.status = status;
    }
}
