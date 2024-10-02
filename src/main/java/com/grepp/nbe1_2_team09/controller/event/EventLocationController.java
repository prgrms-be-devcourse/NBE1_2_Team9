package com.grepp.nbe1_2_team09.controller.event;

import com.grepp.nbe1_2_team09.controller.event.dto.AddEventLocationReq;
import com.grepp.nbe1_2_team09.controller.event.dto.EventLocationDto;
import com.grepp.nbe1_2_team09.controller.event.dto.EventLocationInfoDto;
import com.grepp.nbe1_2_team09.controller.event.dto.UpdateEventLocationReq;
import com.grepp.nbe1_2_team09.domain.service.event.EventLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventLocationController {

    private final EventLocationService eventLocationService;

    @PostMapping("/{eventId}/locations")
    public ResponseEntity<EventLocationDto> addLocationToEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody AddEventLocationReq request) {
        EventLocationDto result = eventLocationService.addLocationToEvent(eventId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{eventId}/locations")
    public ResponseEntity<List<EventLocationInfoDto>> getEventLocations(@PathVariable Long eventId) {
        List<EventLocationInfoDto> locations = eventLocationService.getEventLocations(eventId);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{eventId}/locationsByDate")
    public ResponseEntity<List<EventLocationInfoDto>> getEventLocationsByDate(
            @PathVariable Long eventId,
            @RequestParam("date") LocalDate date) {
        List<EventLocationInfoDto> locations = eventLocationService.getEventLocationByDate(eventId, date);
        return ResponseEntity.ok(locations);
    }


    @PatchMapping("/{eventId}/locations/{locationId}")
    public ResponseEntity<EventLocationDto> updateEventLocation(
            @PathVariable Long eventId,
            @PathVariable Long locationId,
            @Valid @RequestBody UpdateEventLocationReq request) {
        EventLocationDto result = eventLocationService.updateEventLocation(eventId, locationId, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{eventId}/locations/{locationId}")
    public ResponseEntity<Void> removeLocationFromEvent(@PathVariable Long eventId, @PathVariable Long locationId) {
        eventLocationService.removeLocationFromEvent(eventId, locationId);
        return ResponseEntity.noContent().build();
    }
}
