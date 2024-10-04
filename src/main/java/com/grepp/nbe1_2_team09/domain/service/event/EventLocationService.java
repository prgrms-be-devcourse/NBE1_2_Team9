package com.grepp.nbe1_2_team09.domain.service.event;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.EventException;
import com.grepp.nbe1_2_team09.common.exception.exceptions.LocationException;
import com.grepp.nbe1_2_team09.controller.event.dto.AddEventLocationReq;
import com.grepp.nbe1_2_team09.controller.event.dto.EventLocationDto;
import com.grepp.nbe1_2_team09.controller.event.dto.EventLocationInfoDto;
import com.grepp.nbe1_2_team09.controller.event.dto.UpdateEventLocationReq;
import com.grepp.nbe1_2_team09.domain.entity.Location;
import com.grepp.nbe1_2_team09.domain.entity.event.Event;
import com.grepp.nbe1_2_team09.domain.entity.event.EventLocation;
import com.grepp.nbe1_2_team09.domain.repository.event.EventLocationRepository;
import com.grepp.nbe1_2_team09.domain.repository.event.EventRepository;
import com.grepp.nbe1_2_team09.domain.repository.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventLocationService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final EventLocationRepository eventLocationRepository;

    //일정에 장소 추가
    @Transactional
    public EventLocationDto addLocationToEvent(Long eventId, AddEventLocationReq req){
        Event event = findEventByIdOrThrowEventException(eventId);
        Location location = findLocationByIdOrThrowLocationException(req.locationId());

        EventLocation eventLocation = EventLocation.builder()
                .event(event)
                .location(location)
                .description(req.description())
                .visitStartTime(req.visitStartTime())
                .visitEndTime(req.visitEndTime())
                .build();

        EventLocation savedEventLocation = eventLocationRepository.save(eventLocation);
        return EventLocationDto.from(savedEventLocation);
    }



    //일정에 포함된 장소 불러오기
    public List<EventLocationInfoDto> getEventLocations(Long eventId){
        Event event = findEventByIdOrThrowEventException(eventId);

        List<EventLocation> eventLocations = eventLocationRepository.findByEvent(event);

        List<EventLocationInfoDto> infos = eventLocations.stream()
                .map(EventLocationInfoDto::from)
                .collect(Collectors.toList());

        return infos;
    }

    //일정에 포함되고 선택한 날짜랑 같은 장소 불러오기(시간 빠른 순서)
    public List<EventLocationInfoDto> getEventLocationByDate(Long eventId, LocalDate date){
        Event event = findEventByIdOrThrowEventException(eventId);

        List<EventLocation> eventLocations = eventLocationRepository.findByEventAndDateOrderByTime(event,date);

        List<EventLocationInfoDto> infos = eventLocations.stream()
                .map(EventLocationInfoDto::from)
                .collect(Collectors.toList());

        return infos;
    }


    @Transactional
    public EventLocationDto updateEventLocation(Long eventId, Long locationId, UpdateEventLocationReq req){
        Event event = findEventByIdOrThrowEventException(eventId);
        Location location = findLocationByIdOrThrowLocationException(locationId);

        EventLocation eventLocation = findEventLocationOrThrowException(event, location);

        if(req.description() != null){
            eventLocation.updateDescription(req.description());
        }
        if(req.visitStartTime() != null && req.visitEndTime() != null){
            eventLocation.updateVisitTime(req.visitStartTime(), req.visitEndTime());
        }


        return EventLocationDto.from(eventLocation);
    }



    //일정에 포함된 장소 삭제
    @Transactional
    public void removeLocationFromEvent(Long eventId, Long locationId){
        Event event = findEventByIdOrThrowEventException(eventId);
        Location location = findLocationByIdOrThrowLocationException(locationId);

        EventLocation eventLocation = findEventLocationOrThrowException(event, location);

        eventLocationRepository.delete(eventLocation);
    }


    //예외 처리
    private Event findEventByIdOrThrowEventException(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn(">>>> EventId {} : {} <<<<", eventId, ExceptionMessage.EVENT_NOT_FOUND);
                    return new EventException(ExceptionMessage.EVENT_NOT_FOUND);
                });
    }

    private Location findLocationByIdOrThrowLocationException(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> {
                    log.warn(">>>> LocationId {} : {} <<<<", locationId, ExceptionMessage.LOCATION_NOT_FOUND);
                    return new LocationException(ExceptionMessage.LOCATION_NOT_FOUND);
                });
    }

    private EventLocation findEventLocationOrThrowException(Event event, Location location) {
        return eventLocationRepository.findByEventAndLocation(event, location)
                .orElseThrow(() -> {
                    log.warn(">>>> EventLocation not found for Event {} and Location {} : {} <<<<",
                            event.getEventId(), location.getLocationId(), ExceptionMessage.LOCATION_NOT_FOUND);
                    return new LocationException(ExceptionMessage.LOCATION_NOT_FOUND);
                });
    }
}
