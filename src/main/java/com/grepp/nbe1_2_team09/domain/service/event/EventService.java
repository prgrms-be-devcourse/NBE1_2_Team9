package com.grepp.nbe1_2_team09.domain.service.event;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.EventException;
import com.grepp.nbe1_2_team09.common.exception.exceptions.GroupException;
import com.grepp.nbe1_2_team09.controller.event.dto.CreateEventRequest;
import com.grepp.nbe1_2_team09.controller.event.dto.EventDto;
import com.grepp.nbe1_2_team09.controller.event.dto.UpdateEventRequest;
import com.grepp.nbe1_2_team09.domain.entity.Event;
import com.grepp.nbe1_2_team09.domain.entity.Group;
import com.grepp.nbe1_2_team09.domain.repository.event.EventRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public EventDto createEvent(CreateEventRequest request){
        //요청에 있는 groupId로 group을 찾음
        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new EventException(ExceptionMessage.GROUP_NOT_FOUND));
        // 내일 여기에 시작일이 종료일보다 앞인지 확인하는 검사 포함 -> 완료
        // 당일치기 가능성도 있으니까 시작일이 종료일보다 늦지 않은지만 체크
        if(request.startDateTime().isAfter(request.endDateTime())){
            throw new EventException(ExceptionMessage.EVENT_DATE_INVALID);
        }

        //요청 데이터를 기반으로 새로운 event 객체 생성 후 저장
        Event event = Event.builder()
                .eventName(request.eventName())
                .description(request.description())
                .startDateTime(request.startDateTime())
                .endDateTime(request.endDateTime())
                .group(group)
                .build();

        Event savedEvent = eventRepository.save(event);
        return EventDto.from(savedEvent); // 전송을 위해 dto 변환
    }


    public EventDto getEventById(Long id){
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException(ExceptionMessage.EVENT_NOT_FOUND));
        return EventDto.from(event);
    }

    @Transactional
    public EventDto updateEvent(Long eventId, UpdateEventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException(ExceptionMessage.EVENT_NOT_FOUND));

        // 내일 여기에 시작일이 종료일보다 앞인지 확인하는 검사 포함
        if(request.startDateTime().isAfter(request.endDateTime())){
            throw new EventException(ExceptionMessage.EVENT_DATE_INVALID);
        }
        event.updateEventDetails(request.eventName(), request.description(), request.startDateTime(), request.endDateTime());

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
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ExceptionMessage.GROUP_NOT_FOUND));
        return eventRepository.findByGroup(group).stream()
                .map(EventDto::from)
                .collect(Collectors.toList());
    }





}
