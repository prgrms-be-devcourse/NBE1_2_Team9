package com.grepp.nbe1_2_team09.controller.event.dto;


import com.grepp.nbe1_2_team09.domain.entity.event.EventLocation;

public record EventLocationDto(Long eventId, Long locationId, String description) {
    public static EventLocationDto from(EventLocation eventLocation) {
        return new EventLocationDto(
                eventLocation.getEvent().getEventId(),
                eventLocation.getLocation().getLocationId(),
                eventLocation.getDescription()
        );
    }
}
