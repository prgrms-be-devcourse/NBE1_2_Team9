package com.grepp.nbe1_2_team09.domain.service.event;

import com.grepp.nbe1_2_team09.common.exception.exceptions.EventException;
import com.grepp.nbe1_2_team09.common.exception.exceptions.GroupException;
import com.grepp.nbe1_2_team09.controller.event.dto.CreateEventRequest;
import com.grepp.nbe1_2_team09.controller.event.dto.EventDto;
import com.grepp.nbe1_2_team09.controller.event.dto.UpdateEventRequest;
import com.grepp.nbe1_2_team09.domain.entity.Event;
import com.grepp.nbe1_2_team09.domain.entity.Group;
import com.grepp.nbe1_2_team09.domain.repository.event.EventRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private GroupRepository groupRepository;

    private Group testGroup;

    @BeforeEach
    void setUp() {
        testGroup = groupRepository.save(Group.builder()
                .groupName("Test Group")
                .build());
    }

    @Test
    void createEvent_Success() {
        // given
        CreateEventRequest request = new CreateEventRequest(
                "Test Event",
                "This is a test event",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                testGroup.getGroupId()
        );

        // when
        EventDto result = eventService.createEvent(request);

        // then
        assertNotNull(result);
        assertEquals("Test Event", result.eventName());
        assertEquals("This is a test event", result.description());
        assertEquals(request.startDateTime(), result.startDateTime());
        assertEquals(request.endDateTime(), result.endDateTime());
        assertEquals(testGroup.getGroupId(), result.groupId());

        Event savedEvent = eventRepository.findById(result.id()).orElse(null);
        assertNotNull(savedEvent);
        assertEquals("Test Event", savedEvent.getEventName());
        assertEquals("This is a test event", savedEvent.getDescription());
        assertEquals(request.startDateTime(), savedEvent.getStartDateTime());
        assertEquals(request.endDateTime(), savedEvent.getEndDateTime());
        assertEquals(testGroup.getGroupId(), savedEvent.getGroup().getGroupId());
    }

    @Test
    void createEvent_InvalidDateRange() {
        // given
        CreateEventRequest request = new CreateEventRequest(
                "Test Event",
                "This is a test event",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                testGroup.getGroupId()
        );

        // when & then
        assertThrows(EventException.class, () -> eventService.createEvent(request));
    }

    @Test
    void getEventById_Success() {
        // given
        Event event = eventRepository.save(Event.builder()
                .eventName("Test Event")
                .description("This is a test event")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(2))
                .group(testGroup)
                .build());

        // when
        EventDto result = eventService.getEventById(event.getEventId());

        // then
        assertNotNull(result);
        assertEquals(event.getEventId(), result.id());
        assertEquals("Test Event", result.eventName());
        assertEquals("This is a test event", result.description());
        assertEquals(event.getStartDateTime(), result.startDateTime());
        assertEquals(event.getEndDateTime(), result.endDateTime());
        assertEquals(testGroup.getGroupId(), result.groupId());
    }

    @Test
    void getEventById_NotFound() {
        // given
        long nonExistentEventId = 999L;

        // when & then
        assertThrows(EventException.class, () -> eventService.getEventById(nonExistentEventId));
    }

    @Test
    void updateEvent_Success() {
        // given
        Event event = eventRepository.save(Event.builder()
                .eventName("Original Event")
                .description("Original description")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(2))
                .group(testGroup)
                .build());

        UpdateEventRequest request = new UpdateEventRequest(
                "Updated Event",
                "Updated description",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3)
        );

        // when
        EventDto result = eventService.updateEvent(event.getEventId(), request);

        // then
        assertNotNull(result);
        assertEquals("Updated Event", result.eventName());
        assertEquals("Updated description", result.description());
        assertEquals(request.startDateTime(), result.startDateTime());
        assertEquals(request.endDateTime(), result.endDateTime());

        Event updatedEvent = eventRepository.findById(event.getEventId()).orElse(null);
        assertNotNull(updatedEvent);
        assertEquals("Updated Event", updatedEvent.getEventName());
        assertEquals("Updated description", updatedEvent.getDescription());
        assertEquals(request.startDateTime(), updatedEvent.getStartDateTime());
        assertEquals(request.endDateTime(), updatedEvent.getEndDateTime());
    }

    @Test
    void updateEvent_InvalidDateRange() {
        // given
        Event event = eventRepository.save(Event.builder()
                .eventName("Original Event")
                .description("Original description")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(2))
                .group(testGroup)
                .build());

        UpdateEventRequest request = new UpdateEventRequest(
                "Updated Event",
                "Updated description",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(2)
        );

        // when & then
        assertThrows(EventException.class, () -> eventService.updateEvent(event.getEventId(), request));
    }

    @Test
    void deleteEvent_Success() {
        // given
        Event event = eventRepository.save(Event.builder()
                .eventName("To Delete")
                .description("This event will be deleted")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(2))
                .group(testGroup)
                .build());

        // when
        eventService.deleteEvent(event.getEventId());

        // then
        assertFalse(eventRepository.existsById(event.getEventId()));
    }

    @Test
    void getEventsByGroup_Success() {
        // given
        Event event1 = eventRepository.save(Event.builder()
                .eventName("Event 1")
                .description("This is event 1")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(2))
                .group(testGroup)
                .build());

        Event event2 = eventRepository.save(Event.builder()
                .eventName("Event 2")
                .description("This is event 2")
                .startDateTime(LocalDateTime.now().plusDays(3))
                .endDateTime(LocalDateTime.now().plusDays(4))
                .group(testGroup)
                .build());

        // when
        List<EventDto> results = eventService.getEventsByGroup(testGroup.getGroupId());

        // then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(dto -> dto.id().equals(event1.getEventId())));
        assertTrue(results.stream().anyMatch(dto -> dto.id().equals(event2.getEventId())));
    }

    @Test
    void getEventsByGroup_GroupNotFound() {
        // given
        long nonExistentGroupId = 999L;

        // when & then
        assertThrows(GroupException.class, () -> eventService.getEventsByGroup(nonExistentGroupId));
    }
}