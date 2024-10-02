package com.grepp.nbe1_2_team09.domain.entity.event;

import com.grepp.nbe1_2_team09.domain.entity.Location;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_location_tb")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pinId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime visitStartTime;

    @Column(nullable = false)
    private LocalDateTime visitEndTime;

    //기본 비즈니스 메서드

    @Builder
    public EventLocation(Event event, Location location, String description, LocalDateTime visitStartTime, LocalDateTime visitEndTime) {
        this.event = event;
        this.location = location;
        this.description = description;
        this.visitStartTime = visitStartTime;
        this.visitEndTime = visitEndTime;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void changeLocation(Location location) {
        this.location = location;
    }
    public void assignToEvent(Event event) {
        this.event = event;
    }

    public void updateVisitTime(LocalDateTime visitStartTime, LocalDateTime visitEndTime) {
        this.visitStartTime = visitStartTime;
        this.visitEndTime = visitEndTime;
    }
}
