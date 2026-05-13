package com.bazuuyu.b2b.wholesale.entity;

import com.bazuuyu.b2b.wholesale.entity.enums.FunnelEventType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "funnel_events")
public class FunnelEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private FunnelEventType eventType;

    @Column(length = 60)
    private String source;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public FunnelEventType getEventType() { return eventType; }
    public void setEventType(FunnelEventType eventType) { this.eventType = eventType; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
