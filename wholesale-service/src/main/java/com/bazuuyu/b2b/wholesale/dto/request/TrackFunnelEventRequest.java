package com.bazuuyu.b2b.wholesale.dto.request;

import com.bazuuyu.b2b.wholesale.entity.enums.FunnelEventType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class TrackFunnelEventRequest {
    @Email
    @NotNull
    private String email;
    @NotNull
    private FunnelEventType eventType;
    private String source;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public FunnelEventType getEventType() { return eventType; }
    public void setEventType(FunnelEventType eventType) { this.eventType = eventType; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
