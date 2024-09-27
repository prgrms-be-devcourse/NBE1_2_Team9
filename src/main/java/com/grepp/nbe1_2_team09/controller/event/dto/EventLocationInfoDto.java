package com.grepp.nbe1_2_team09.controller.event.dto;

import com.grepp.nbe1_2_team09.controller.location.dto.LocationEventDto;
import com.grepp.nbe1_2_team09.domain.entity.EventLocation;

public record EventLocationInfoDto(
        LocationEventDto location,
        String description
) {
    public static EventLocationInfoDto from(EventLocation eventLocation) {
        return new EventLocationInfoDto(
                LocationEventDto.from(eventLocation.getLocation()),
                eventLocation.getDescription()
        );
    }
}
