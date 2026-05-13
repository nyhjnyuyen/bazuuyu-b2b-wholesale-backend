package com.bazuuyu.b2b.wholesale.dto.request;

import com.bazuuyu.b2b.wholesale.entity.enums.LeadStage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class UpsertLeadRequest {
    @Email
    @NotNull
    private String email;
    private String companyName;
    private LeadStage stage;
    private String note;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public LeadStage getStage() { return stage; }
    public void setStage(LeadStage stage) { this.stage = stage; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
