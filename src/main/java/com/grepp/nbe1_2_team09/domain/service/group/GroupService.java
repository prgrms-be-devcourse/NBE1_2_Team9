package com.grepp.nbe1_2_team09.domain.service.group;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.GroupException;
import com.grepp.nbe1_2_team09.common.exception.exceptions.UserException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    // 관리자 유저 정보 받아서 그룹 생성
    @Transactional
    public GroupDto createGroup(CreateGroupRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new UserException(ExceptionMessage.USER_NOT_FOUND));

        Group group = Group.builder()
                .groupName(request.groupName())
                .build();
        Group savedGroup = groupRepository.save(group);

        GroupMembership membership = GroupMembership.builder()
                .group(savedGroup)
                .user(creator)
                .role(Role.ADMIN)//만든 사람은 처음에 관리자로 설정됨
                .build();
        groupMembershipRepository.save(membership);

        return GroupDto.from(savedGroup);
    }

    //groupId를 통해 group 조회
    public GroupDto getGroupById(Long id) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new GroupException(ExceptionMessage.GROUP_NOT_FOUND));
        return GroupDto.from(group);
    }

    //사용자가 참여한 그룹의 리스트 조회
    public List<GroupDto> getUserGroups(Long userId) {

        return groupMembershipRepository.findByUser_Id(userId).stream()
                .map(GroupMembership::getGroup)//User로 group을 찾음
                .map(GroupDto::from)//그리고 dto로 변환
                .collect(Collectors.toList());

    }

    @Transactional
    public GroupDto updateGroup(Long id, UpdateGroupRequest request) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new GroupException(ExceptionMessage.GROUP_NOT_FOUND));
        group.updateGroupName(request.groupName());
        Group updatedGroup = groupRepository.save(group);
        return GroupDto.from(updatedGroup);
    }

    @Transactional
    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new GroupException(ExceptionMessage.GROUP_NOT_FOUND);
        }
        groupRepository.deleteById(id);
    }

    //그룹에 멤버 추가 기능
    @Transactional
    public void addMemberToGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ExceptionMessage.GROUP_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ExceptionMessage.USER_NOT_FOUND));

        //추가하려고 하는데 해당 사용자가 이미 그룹에 속해있는 경우
        if (groupMembershipRepository.existsByGroupAndUser(group, user)) {
            throw new GroupException(ExceptionMessage.USER_ALREADY_IN_GROUP);

        }

        GroupMembership membership = GroupMembership.builder()
                .group(group)
                .user(user)
                .role(Role.MEMBER) // 추가되는 사람은 처음에는 MEMBER로 고정 이후에 변경 가능
                .build();
        groupMembershipRepository.save(membership);
    }

    @Transactional
    public void changeGroupMemberRole(Long groupId, Long userId, Role role) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ExceptionMessage.GROUP_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ExceptionMessage.USER_NOT_FOUND));

        GroupMembership membership = groupMembershipRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new GroupException(ExceptionMessage.USER_NOT_IN_GROUP));

        //만약 관리자가 역할을 변경하고자 할 때, 관리자가 1명이라면 역할을 바꾸지 못하도록 설정
        if(membership.getRole() == Role.ADMIN && role != Role.ADMIN){

            List<GroupMembership> memberships = groupMembershipRepository.findByGroup(group);
            long adminCount = memberships.stream()
                    .filter(m -> m.getRole() == Role.ADMIN)
                    .count();
            if (adminCount == 1) {
                throw new GroupException(ExceptionMessage.CANNOT_REMOVE_LAST_ADMIN);
            }

        }

        membership.changeRole(role);


    }

    @Transactional
    public void removeMemberFromGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ExceptionMessage.GROUP_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ExceptionMessage.USER_NOT_FOUND));

        GroupMembership membership = groupMembershipRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new UserException(ExceptionMessage.USER_NOT_IN_GROUP));

    }

    public List<GroupMembershipDto> getGroupMembers(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ExceptionMessage.GROUP_NOT_FOUND));

        return groupMembershipRepository.findByGroup(group).stream()
                .map(GroupMembershipDto::from)
                .collect(Collectors.toList());
    }


}
