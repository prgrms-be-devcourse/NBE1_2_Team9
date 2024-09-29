package com.grepp.nbe1_2_team09.controller.event.dto;

import com.grepp.nbe1_2_team09.domain.entity.event.Event;

import java.time.LocalDateTime;

public record EventDto(Long id, String eventName, String description,
                       LocalDateTime startDateTime, LocalDateTime endDateTime, Long groupId) {
    public static EventDto from(Event event) {
        return new EventDto(
                event.getEventId(),
                event.getEventName(),
                event.getDescription(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getGroup().getGroupId()
        );
    }
}
