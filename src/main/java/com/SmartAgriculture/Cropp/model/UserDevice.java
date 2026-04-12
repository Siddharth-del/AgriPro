package com.SmartAgriculture.Cropp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_devices",
       uniqueConstraints = @UniqueConstraint(columnNames = {"device_id", "user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId;

    @Column(name = "device_api_key", nullable = false)
    private String deviceApiKey;

    @Column(name = "device_label")
    private String deviceLabel;          // e.g. "Field A Sensor"

    @Column(name = "city")
    private String city;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }
}