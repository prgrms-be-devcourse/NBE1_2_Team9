package com.grepp.nbe1_2_team09.domain.service.group;

import com.grepp.nbe1_2_team09.common.exception.exceptions.GroupException;
import com.grepp.nbe1_2_team09.controller.group.dto.CreateGroupRequest;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupDto;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupMembershipDto;
import com.grepp.nbe1_2_team09.controller.group.dto.UpdateGroupRequest;
import com.grepp.nbe1_2_team09.domain.entity.Role;
import com.grepp.nbe1_2_team09.domain.entity.User;
import com.grepp.nbe1_2_team09.domain.entity.group.GroupRole;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupInvitationRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import com.grepp.nbe1_2_team09.domain.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupInvitationRepository groupInvitationRepository;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testUser")
                .email("test@example.com")
                .password("1234")
                .role(Role.MEMBER)
                .build();
        userRepository.save(testUser);

        testUser2 = User.builder()
                .username("testUser2")
                .email("test2@example.com")
                .password("1234")
                .role(Role.MEMBER)
                .build();
        userRepository.save(testUser2);
    }

    @Test
    void createGroup() {
        // Given
        CreateGroupRequest req = new CreateGroupRequest("Test Group");

        // When
        GroupDto result = groupService.createGroup(req, testUser.getUserId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.groupName()).isEqualTo("Test Group");
        assertThat(result.groupId()).isNotNull();
        assertThat(result.creationDate()).isNotNull();
    }

    @Test
    void getGroupById() {
        // Given
        GroupDto group = groupService.createGroup(new CreateGroupRequest("Test Group"), testUser.getUserId());

        // When
        GroupDto result = groupService.getGroupById(group.groupId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.groupId()).isEqualTo(group.groupId());
        assertThat(result.creationDate()).isNotNull();
        assertThat(result.creationDate()).isEqualTo(group.creationDate());
    }

    @Test
    void getUserGroups() {
        // Given
        groupService.createGroup(new CreateGroupRequest("Test Group1"), testUser.getUserId());
        groupService.createGroup(new CreateGroupRequest("Test Group2"), testUser.getUserId());

        // When
        List<GroupDto> result = groupService.getUserGroups(testUser.getUserId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(GroupDto::groupName).contains("Test Group1", "Test Group2");
    }

    @Test
    void updateGroup() {
        // Given
        GroupDto group = groupService.createGroup(new CreateGroupRequest("Test Group"), testUser.getUserId());
        UpdateGroupRequest req = new UpdateGroupRequest("Test Group updated");

        // When
        GroupDto result = groupService.updateGroup(group.groupId(), req);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.groupName()).isEqualTo("Test Group updated");
    }

    @Test
    void deleteGroup() {
        // Given
        GroupDto group = groupService.createGroup(new CreateGroupRequest("Test Group"), testUser.getUserId());

        // When
        groupService.deleteGroup(group.groupId());

        // Then
        assertThatThrownBy(() -> groupService.getGroupById(group.groupId()))
                .isInstanceOf(GroupException.class)
                .hasMessageContaining("GROUP_NOT_FOUND");
    }


    @Test
    void getGroupMembers() {
        // Given
        GroupDto group = groupService.createGroup(new CreateGroupRequest("Test Group"), testUser.getUserId());

        // When
        List<GroupMembershipDto> result = groupService.getGroupMembers(group.groupId());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(testUser.getUserId());
        assertThat(result.get(0).role()).isEqualTo(GroupRole.ADMIN);
    }
}