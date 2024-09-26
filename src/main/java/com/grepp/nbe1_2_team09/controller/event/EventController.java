package com.grepp.nbe1_2_team09.controller.event;

import com.grepp.nbe1_2_team09.controller.event.dto.CreateEventRequest;
import com.grepp.nbe1_2_team09.controller.event.dto.EventDto;
import com.grepp.nbe1_2_team09.controller.event.dto.UpdateEventRequest;
import com.grepp.nbe1_2_team09.domain.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody CreateEventRequest request){
        EventDto eventDto = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventDto);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long eventId){
        EventDto eventDto = eventService.getEventById(eventId);
        return ResponseEntity.ok().body(eventDto);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long eventId, @RequestBody UpdateEventRequest request){
        EventDto eventDto = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok().body(eventDto);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<EventDto>> getGroupEvents(@PathVariable Long groupId){
        List<EventDto> eventDtos = eventService.getEventsByGroup(groupId);
        return ResponseEntity.ok().body(eventDtos);
    }


}
