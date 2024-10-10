package com.grepp.nbe1_2_team09.domain.service.event;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.EventException;
import com.grepp.nbe1_2_team09.common.exception.exceptions.GroupException;
import com.grepp.nbe1_2_team09.controller.event.dto.CreateEventRequest;
import com.grepp.nbe1_2_team09.controller.event.dto.EventDto;
import com.grepp.nbe1_2_team09.controller.event.dto.UpdateEventRequest;
import com.grepp.nbe1_2_team09.domain.entity.event.Event;
import com.grepp.nbe1_2_team09.domain.entity.group.Group;
import com.grepp.nbe1_2_team09.domain.repository.event.eventrepo.EventRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public EventDto createEvent(CreateEventRequest request){
        //요청에 있는 groupId로 group을 찾음
        Group group = findGroupByIdOrThrowGroupException(request.groupId());

        //요청 데이터를 기반으로 새로운 event 객체 생성 후 저장
        Event event = Event.builder()
                .eventName(request.eventName())
                .description(request.description())
                .city(request.city())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .group(group)
                .build();

        Event savedEvent = eventRepository.save(event);
        return EventDto.from(savedEvent); // 전송을 위해 dto 변환
    }


    public EventDto getEventById(Long id){
        Event event = findByIdOrThrowEventException(id);
        return EventDto.from(event);
    }

    @Transactional
    public EventDto updateEvent(Long eventId, UpdateEventRequest request) {
        Event event = findByIdOrThrowEventException(eventId);

        event.updateEventDetails(request.eventName(), request.description(), request.startDate(), request.endDate());

        return EventDto.from(event);
    }



    @Transactional
    public void deleteEvent(Long eventId){
        if(!eventRepository.existsById(eventId)){
            throw new EventException(ExceptionMessage.EVENT_NOT_FOUND);
        }
        eventRepository.deleteById(eventId);
    }


    public List<EventDto> getEventsByGroup(Long groupId){
        Group group = findGroupByIdOrThrowGroupException(groupId);
        return eventRepository.findByGroup(group).stream()
                .map(EventDto::from)
                .collect(Collectors.toList());
    }


    //예외처리

    private Event findByIdOrThrowEventException(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn(">>>> EventId {} : {} <<<<", eventId, ExceptionMessage.EVENT_NOT_FOUND);
                    return new EventException(ExceptionMessage.EVENT_NOT_FOUND);
                });
    }

    private Group findGroupByIdOrThrowGroupException(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.warn(">>>> GroupId {} : {} <<<<", groupId, ExceptionMessage.GROUP_NOT_FOUND);
                    return new GroupException(ExceptionMessage.GROUP_NOT_FOUND);
                });
    }





}
