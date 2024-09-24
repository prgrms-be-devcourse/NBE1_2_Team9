package com.grepp.nbe1_2_team09.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pin_tb")
@Getter
@Setter
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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(columnDefinition = "TEXT")
    private String description;

    //기본 비즈니스 메서드

    @Builder
    public EventLocation(Event event, Location location, String description) {
        this.event = event;
        this.location = location;
        this.description = description;
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
}
