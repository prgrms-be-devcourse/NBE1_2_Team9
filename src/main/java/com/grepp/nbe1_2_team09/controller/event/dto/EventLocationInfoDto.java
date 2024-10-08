package com.grepp.nbe1_2_team09.controller.event.dto;

import com.grepp.nbe1_2_team09.controller.location.dto.LocationEventDto;
import com.grepp.nbe1_2_team09.domain.entity.event.EventLocation;

import java.time.LocalDateTime;

public record EventLocationInfoDto(
        LocationEventDto location,
        String description,
        LocalDateTime visitStartTime,
        LocalDateTime visitEndTime
) {
    public static EventLocationInfoDto from(EventLocation eventLocation) {
        return new EventLocationInfoDto(
                LocationEventDto.from(eventLocation.getLocation()),
                eventLocation.getDescription(),
                eventLocation.getVisitStartTime(),
                eventLocation.getVisitEndTime()
        );
    }
}
