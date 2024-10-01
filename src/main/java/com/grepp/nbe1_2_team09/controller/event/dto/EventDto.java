package com.grepp.nbe1_2_team09.controller.event.dto;

import com.grepp.nbe1_2_team09.domain.entity.event.Event;

import java.time.LocalDate;

public record EventDto(Long id, String eventName, String description,String city,
                       LocalDate startDate, LocalDate endDate, Long groupId) {
    public static EventDto from(Event event) {
        return new EventDto(
                event.getEventId(),
                event.getEventName(),
                event.getDescription(),
                event.getCity(),
                event.getStartDate(),
                event.getEndDate(),
                event.getGroup().getGroupId()
        );
    }
}
