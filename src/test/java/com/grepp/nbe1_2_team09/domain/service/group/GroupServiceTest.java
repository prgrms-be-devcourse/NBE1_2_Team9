package com.grepp.nbe1_2_team09.domain.service.group;


import com.grepp.nbe1_2_team09.common.exception.exceptions.GroupException;
import com.grepp.nbe1_2_team09.controller.group.dto.CreateGroupRequest;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupDto;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupMembershipDto;
import com.grepp.nbe1_2_team09.controller.group.dto.UpdateGroupRequest;
import com.grepp.nbe1_2_team09.domain.entity.Group;
import com.grepp.nbe1_2_team09.domain.entity.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.Role;
import com.grepp.nbe1_2_team09.domain.entity.User;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupMembershipRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import com.grepp.nbe1_2_team09.domain.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User("testUser", "test@example.com", "password"));
    }

    @Test
    void createGroup_Success() {
        // given
        CreateGroupRequest request = new CreateGroupRequest("Test Group");

        // when
        GroupDto result = groupService.createGroup(request, testUser.getUserId());

        // then
        assertNotNull(result);
        assertEquals("Test Group", result.groupName());

        Group savedGroup = groupRepository.findById(result.groupId()).orElse(null);
        assertNotNull(savedGroup);
        assertEquals("Test Group", savedGroup.getGroupName());

        assertTrue(groupMembershipRepository.existsByGroupAndUser(savedGroup, testUser));
    }

    @Test
    void getGroupById_Success() {
        // given
        Group group = groupRepository.save(Group.builder().groupName("Test Group").build());

        // when
        GroupDto result = groupService.getGroupById(group.getGroupId());

        // then
        assertNotNull(result);
        assertEquals(group.getGroupId(), result.groupId());
        assertEquals(group.getGroupName(), result.groupName());
    }

    @Test
    void getGroupById_NotFound() {
        // given
        Long nonExistentGroupId = 999L;

        // when & then
        assertThrows(GroupException.class, () -> groupService.getGroupById(nonExistentGroupId));
    }

    @Test
    void getUserGroups_Success() {
        // given
        Group group1 = groupRepository.save(Group.builder().groupName("Group 1").build());
        Group group2 = groupRepository.save(Group.builder().groupName("Group 2").build());

        groupService.addMemberToGroup(group1.getGroupId(), testUser.getUserId());
        groupService.addMemberToGroup(group2.getGroupId(), testUser.getUserId());

        // when
        List<GroupDto> results = groupService.getUserGroups(testUser.getUserId());

        // then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(dto -> dto.groupName().equals("Group 1")));
        assertTrue(results.stream().anyMatch(dto -> dto.groupName().equals("Group 2")));
    }

    @Test
    void updateGroup_Success() {
        // given
        Group group = groupRepository.save(Group.builder().groupName("Original Name").build());
        UpdateGroupRequest request = new UpdateGroupRequest("Updated Name");

        // when
        GroupDto result = groupService.updateGroup(group.getGroupId(), request);

        // then
        assertNotNull(result);
        assertEquals("Updated Name", result.groupName());

        Group updatedGroup = groupRepository.findById(group.getGroupId()).orElse(null);
        assertNotNull(updatedGroup);
        assertEquals("Updated Name", updatedGroup.getGroupName());
    }

    @Test
    void deleteGroup_Success() {
        // given
        Group group = groupRepository.save(Group.builder().groupName("To Delete").build());

        // when
        groupService.deleteGroup(group.getGroupId());

        // then
        assertFalse(groupRepository.existsById(group.getGroupId()));
    }

    @Test
    void addMemberToGroup_Success() {
        // given
        Group group = groupRepository.save(Group.builder().groupName("Test Group").build());
        User newMember = userRepository.save(new User("newMember", "new@example.com", "password"));

        // when
        groupService.addMemberToGroup(group.getGroupId(), newMember.getUserId());

        // then
        assertTrue(groupMembershipRepository.existsByGroupAndUser(group, newMember));
    }

    @Test
    void addMemberToGroup_AlreadyMember() {
        // given
        Group group = groupRepository.save(Group.builder().groupName("Test Group").build());
        groupService.addMemberToGroup(group.getGroupId(), testUser.getUserId());

        // when & then
        assertThrows(GroupException.class, () ->
                groupService.addMemberToGroup(group.getGroupId(), testUser.getUserId())
        );
    }

    @Test
    void changeGroupMemberRole_Success() {
        // given
        Group group = groupRepository.save(Group.builder().groupName("Test Group").build());
        groupService.addMemberToGroup(group.getGroupId(), testUser.getUserId());

        // when
        groupService.changeGroupMemberRole(group.getGroupId(), testUser.getUserId(), Role.ADMIN);

        // then
        Optional<GroupMembership> membership = groupMembershipRepository.findByGroupAndUser(group, testUser);
        assertTrue(membership.isPresent());
        assertEquals(Role.ADMIN, membership.get().getRole());
    }

    @Test
    void changeGroupMemberRole_CannotRemoveLastAdmin() {
        // given
        CreateGroupRequest request = new CreateGroupRequest("Admin Group");
        GroupDto groupDto = groupService.createGroup(request, testUser.getUserId());

        // when & then
        assertThrows(GroupException.class, () ->
                groupService.changeGroupMemberRole(groupDto.groupId(), testUser.getUserId(), Role.MEMBER)
        );
    }

    @Test
    void getGroupMembers_Success() {
        // given
        Group group = groupRepository.save(Group.builder().groupName("Test Group").build());
        User member1 = userRepository.save(new User("member1", "member1@example.com", "password"));
        User member2 = userRepository.save(new User("member2", "member2@example.com", "password"));

        groupService.addMemberToGroup(group.getGroupId(), member1.getUserId());
        groupService.addMemberToGroup(group.getGroupId(), member2.getUserId());

        // when
        List<GroupMembershipDto> members = groupService.getGroupMembers(group.getGroupId());

        // then
        System.out.println(members.get(0));
        System.out.println(members.get(1));
        assertEquals(2, members.size());

        assertTrue(members.stream().anyMatch(dto -> dto.userId().equals(member1.getUserId())));
        assertTrue(members.stream().anyMatch(dto -> dto.userId().equals(member2.getUserId())));
    }
}