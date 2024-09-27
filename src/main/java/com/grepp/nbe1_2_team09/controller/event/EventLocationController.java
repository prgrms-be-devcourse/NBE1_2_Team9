package com.grepp.nbe1_2_team09.controller.event;

import com.grepp.nbe1_2_team09.controller.event.dto.EventLocationDto;
import com.grepp.nbe1_2_team09.controller.event.dto.EventLocationInfoDto;
import com.grepp.nbe1_2_team09.domain.service.event.EventLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventLocationController {

    private final EventLocationService eventLocationService;

    @PostMapping("/{eventId}/locations/{locationId}")
    public ResponseEntity<EventLocationDto> addLocationToEvent(@PathVariable Long eventId, @PathVariable Long locationId) {
        EventLocationDto result = eventLocationService.addLocationToEvent(eventId, locationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{eventId}/locations")
    public ResponseEntity<List<EventLocationInfoDto>> getEventLocations(@PathVariable Long eventId) {
        List<EventLocationInfoDto> locations = eventLocationService.getEventLocations(eventId);
        return ResponseEntity.ok(locations);
    }

    //description에 대한 정보만 변경하면 되니까 description
    @PatchMapping("/{eventId}/locations/{locationId}")
    public ResponseEntity<EventLocationDto> updateDescription(@PathVariable Long eventId, @PathVariable Long locationId, @RequestBody String description) {
        EventLocationDto result = eventLocationService.updateDescription(eventId, locationId, description);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{eventId}/locations/{locationId}")
    public ResponseEntity<Void> removeLocationFromEvent(@PathVariable Long eventId, @PathVariable Long locationId) {
        eventLocationService.removeLocationFromEvent(eventId, locationId);
        return ResponseEntity.noContent().build();
    }
}
