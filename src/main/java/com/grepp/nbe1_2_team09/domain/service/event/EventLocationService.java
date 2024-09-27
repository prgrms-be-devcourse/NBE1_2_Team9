package com.grepp.nbe1_2_team09.domain.service.event;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.EventException;
import com.grepp.nbe1_2_team09.common.exception.exceptions.LocationException;
import com.grepp.nbe1_2_team09.controller.event.dto.EventLocationDto;
import com.grepp.nbe1_2_team09.domain.entity.Event;
import com.grepp.nbe1_2_team09.domain.entity.EventLocation;
import com.grepp.nbe1_2_team09.domain.entity.Location;
import com.grepp.nbe1_2_team09.domain.repository.event.EventLocationRepository;
import com.grepp.nbe1_2_team09.domain.repository.event.EventRepository;
import com.grepp.nbe1_2_team09.domain.repository.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventLocationService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final EventLocationRepository eventLocationRepository;

    //일정에 장소 추가
    public EventLocationDto addLocationToEvent(Long eventId, Long locationId){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException(ExceptionMessage.EVENT_NOT_FOUND));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationException(ExceptionMessage.LOCATION_NOT_FOUND));

        EventLocation eventLocation = EventLocation.builder()
                .event(event)
                .location(location)
                .build();

        EventLocation savedEventLocation = eventLocationRepository.save(eventLocation);
        return EventLocationDto.from(savedEventLocation);
    }



    //일정에 포함된 장소 불러오기



    //일정에 추가된 장소에 대한 description 추가



    //일정에 포함된 장소 삭제
}
