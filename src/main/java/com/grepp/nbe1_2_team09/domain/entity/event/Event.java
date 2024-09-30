package com.grepp.nbe1_2_team09.domain.entity.event;

import com.grepp.nbe1_2_team09.domain.entity.group.Group;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event_tb")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<EventLocation> eventLocations = new ArrayList<>();


    //비즈니스 메서드

    @Builder
    public Event(String eventName, String description, String city, LocalDate startDate, LocalDate endDate, Group group) {
        this.eventName = eventName;
        this.description = description;
        this.city = city;
        this.status = EventStatus.UPCOMING;
        this.startDate = startDate;
        this.endDate = endDate;
        this.group = group;
    }

    public void updateEventDetails(String eventName, String description, LocalDate startDate, LocalDate endDate ){
        this.eventName = eventName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateStatus(EventStatus status){
        this.status = status;
    }


}
