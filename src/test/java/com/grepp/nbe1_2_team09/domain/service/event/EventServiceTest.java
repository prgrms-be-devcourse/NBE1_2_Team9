package com.grepp.nbe1_2_team09.domain.service.event;

import com.grepp.nbe1_2_team09.common.exception.exceptions.EventException;
import com.grepp.nbe1_2_team09.controller.event.dto.CreateEventRequest;
import com.grepp.nbe1_2_team09.controller.event.dto.EventDto;
import com.grepp.nbe1_2_team09.controller.event.dto.UpdateEventRequest;
import com.grepp.nbe1_2_team09.controller.group.dto.CreateGroupRequest;
import com.grepp.nbe1_2_team09.domain.entity.Role;
import com.grepp.nbe1_2_team09.domain.entity.User;
import com.grepp.nbe1_2_team09.domain.entity.group.Group;
import com.grepp.nbe1_2_team09.domain.repository.event.EventRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import com.grepp.nbe1_2_team09.domain.repository.user.UserRepository;
import com.grepp.nbe1_2_team09.domain.service.group.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private EventRepository eventRepository;

    private User testUser;
    private Group testGroup;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("1234")
                .role(Role.MEMBER)
                .build();
        userRepository.save(testUser);

        CreateGroupRequest groupRequest = new CreateGroupRequest("Test Group");
        Long groupId = groupService.createGroup(groupRequest, testUser.getUserId()).groupId();
        testGroup = groupRepository.findById(groupId).orElseThrow();
    }

    @Test
    void createEvent() {
        // Given
        CreateEventRequest request = new CreateEventRequest(
                "Test Event",
                "Test Description",
                "Test City",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                testGroup.getGroupId()
        );

        // When
        EventDto result = eventService.createEvent(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.eventName()).isEqualTo("Test Event");
        assertThat(result.description()).isEqualTo("Test Description");
        assertThat(result.city()).isEqualTo("Test City");
        assertThat(result.startDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(result.endDate()).isEqualTo(LocalDate.now().plusDays(3));
        assertThat(result.groupId()).isEqualTo(testGroup.getGroupId());
    }

    @Test
    void getEventById() {
        // Given
        EventDto createdEvent = createTestEvent();

        // When
        EventDto result = eventService.getEventById(createdEvent.id());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(createdEvent.id());
        assertThat(result.eventName()).isEqualTo("Test Event");
    }

    @Test
    void updateEvent() {
        // Given
        EventDto createdEvent = createTestEvent();
        UpdateEventRequest updateRequest = new UpdateEventRequest(
                "Updated Event",
                "Updated Description",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4)
        );

        // When
        EventDto result = eventService.updateEvent(createdEvent.id(), updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.eventName()).isEqualTo("Updated Event");
        assertThat(result.description()).isEqualTo("Updated Description");
        assertThat(result.startDate()).isEqualTo(LocalDate.now().plusDays(2));
        assertThat(result.endDate()).isEqualTo(LocalDate.now().plusDays(4));
    }

    @Test
    void deleteEvent() {
        // Given
        EventDto createdEvent = createTestEvent();

        // When
        eventService.deleteEvent(createdEvent.id());

        // Then
        assertThatThrownBy(() -> eventService.getEventById(createdEvent.id()))
                .isInstanceOf(EventException.class)
                .hasMessageContaining("EVENT_NOT_FOUND");
    }

    @Test
    void getEventsByGroup() {
        // Given
        createTestEvent();
        createTestEvent();

        // When
        List<EventDto> result = eventService.getEventsByGroup(testGroup.getGroupId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(EventDto::eventName).containsOnly("Test Event");
    }

    private EventDto createTestEvent() {
        CreateEventRequest request = new CreateEventRequest(
                "Test Event",
                "Test Description",
                "Test City",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                testGroup.getGroupId()
        );
        return eventService.createEvent(request);
    }
}