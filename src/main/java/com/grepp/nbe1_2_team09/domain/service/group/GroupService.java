package com.grepp.nbe1_2_team09.domain.service.group;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.GroupException;
import com.grepp.nbe1_2_team09.common.exception.exceptions.UserException;
import com.grepp.nbe1_2_team09.controller.group.dto.CreateGroupRequest;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupDto;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupMembershipDto;
import com.grepp.nbe1_2_team09.controller.group.dto.UpdateGroupRequest;
import com.grepp.nbe1_2_team09.domain.entity.*;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupMembershipRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import com.grepp.nbe1_2_team09.domain.repository.user.UserRepository;
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
public class GroupService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    // 관리자 유저 정보 받아서 그룹 생성
    @Transactional
    public GroupDto createGroup(CreateGroupRequest request, Long creatorId) {
        User creator = findUserByIdOrThrowUserException(creatorId);

        Group group = Group.builder()
                .groupName(request.groupName())
                .build();
        Group savedGroup = groupRepository.save(group);

        GroupMembership membership = GroupMembership.builder()
                .group(savedGroup)
                .user(creator)
                .role(GroupRole.ADMIN)//만든 사람은 처음에 관리자로 설정됨
                .build();
        groupMembershipRepository.save(membership);

        return GroupDto.from(savedGroup);
    }

    //groupId를 통해 group 조회
    public GroupDto getGroupById(Long id) {
        Group group = findGroupByIdOrThrowGroupException(id);
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
        Group group = findGroupByIdOrThrowGroupException(id);
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
        Group group = findGroupByIdOrThrowGroupException(groupId);
        User user = findUserByIdOrThrowUserException(userId);

        //추가하려고 하는데 해당 사용자가 이미 그룹에 속해있는 경우
        if (groupMembershipRepository.existsByGroupAndUser(group, user)) {
            throw new GroupException(ExceptionMessage.USER_ALREADY_IN_GROUP);
        }

        GroupMembership membership = GroupMembership.builder()
                .group(group)
                .user(user)
                .role(GroupRole.MEMBER) // 추가되는 사람은 처음에는 MEMBER로 고정 이후에 변경 가능
                .build();
        groupMembershipRepository.save(membership);
    }

    @Transactional
    public void changeGroupMemberRole(Long groupId, Long userId, GroupRole role) {
        Group group = findGroupByIdOrThrowGroupException(groupId);
        User user = findUserByIdOrThrowUserException(userId);
        GroupMembership membership = findGroupMemberByIdThrowGroupException(group, user);

        //만약 관리자가 역할을 변경하고자 할 때, 관리자가 1명이라면 역할을 바꾸지 못하도록 설정
        if(membership.getRole() == GroupRole.ADMIN && role != GroupRole.ADMIN){

            List<GroupMembership> memberships = groupMembershipRepository.findByGroup(group);
            long adminCount = memberships.stream()
                    .filter(m -> m.getRole() == GroupRole.ADMIN)
                    .count();
            if (adminCount == 1) {
                throw new GroupException(ExceptionMessage.CANNOT_REMOVE_LAST_ADMIN);
            }

        }

        membership.changeRole(role);


    }

    //이거 관리자 전용하고 사용자의 자의 탈퇴 구분할지 결정
    @Transactional
    public void removeMemberFromGroup(Long groupId, Long userId) {
        Group group = findGroupByIdOrThrowGroupException(groupId);
        User user = findUserByIdOrThrowUserException(userId);
        GroupMembership membership = findGroupMemberByIdThrowGroupException(group, user);

        groupMembershipRepository.delete(membership);

    }

    public List<GroupMembershipDto> getGroupMembers(Long groupId) {
        Group group = findGroupByIdOrThrowGroupException(groupId);

        return groupMembershipRepository.findByGroup(group).stream()
                .map(GroupMembershipDto::from)
                .collect(Collectors.toList());
    }



    //예외처리를 포함한 검색 함수들

    private User findUserByIdOrThrowUserException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->{
                    log.warn(">>>> UserId {} : {} <<<<", userId, ExceptionMessage.USER_NOT_FOUND);
                            return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });
    }

    private Group findGroupByIdOrThrowGroupException(Long groupId){
        return groupRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.warn(">>>> GroupId {} : {} <<<<", groupId, ExceptionMessage.GROUP_NOT_FOUND);
                    return new GroupException(ExceptionMessage.GROUP_NOT_FOUND);
                });
    }

    private GroupMembership findGroupMemberByIdThrowGroupException(Group group, User user) {
        return groupMembershipRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> {
                    log.warn(">>>> User{} not in Group {} : {}", user.getUserId(), group.getGroupId(), ExceptionMessage.USER_NOT_FOUND);
                    return new GroupException(ExceptionMessage.USER_NOT_IN_GROUP);
                });
    }


}
