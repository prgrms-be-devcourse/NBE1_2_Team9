package com.grepp.nbe1_2_team09.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event_tb")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false, length = 100)
    private String eventName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<EventLocation> eventLocations = new ArrayList<>();


    //비즈니스 메서드

    @Builder
    public Event(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime, Group group) {
        this.eventName = eventName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.group = group;
    }

    public void updateEventDetails(String eventName, String description, LocalDateTime startDateTime, LocalDateTime endDateTime ){
        this.eventName = eventName;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }


}
