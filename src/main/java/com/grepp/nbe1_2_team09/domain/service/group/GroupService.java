package com.grepp.nbe1_2_team09.domain.service.group;

import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.GroupException;
import com.grepp.nbe1_2_team09.common.exception.exceptions.UserException;
import com.grepp.nbe1_2_team09.controller.group.dto.CreateGroupRequest;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupDto;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupMembershipDto;
import com.grepp.nbe1_2_team09.controller.group.dto.UpdateGroupRequest;
import com.grepp.nbe1_2_team09.domain.entity.group.Group;
import com.grepp.nbe1_2_team09.domain.entity.group.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.group.GroupRole;
import com.grepp.nbe1_2_team09.domain.entity.group.invitation.GroupInvitation;
import com.grepp.nbe1_2_team09.domain.entity.user.User;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupInvitationRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupMembershipRepository;
import com.grepp.nbe1_2_team09.domain.repository.group.GroupRepository;
import com.grepp.nbe1_2_team09.domain.repository.user.UserRepository;
import com.grepp.nbe1_2_team09.notification.controller.dto.NotificationDto;
import com.grepp.nbe1_2_team09.notification.service.NotificationService;
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
    private final GroupInvitationRepository groupInvitationRepository;
    private final NotificationService notificationService;

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
                .role(GroupRole.ADMIN)
                .build();
        groupMembershipRepository.save(membership);

        return GroupDto.from(savedGroup);
    }

    public GroupDto getGroupById(Long id) {
        Group group = findGroupByIdOrThrowGroupException(id);
        return GroupDto.from(group);
    }

    public List<GroupDto> getUserGroups(Long userId) {
        return groupMembershipRepository.findByUser_Id(userId).stream()
                .map(GroupMembership::getGroup)
                .map(GroupDto::from)
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

    @Transactional
    public void inviteMemberToGroup(Long groupId, String email, Long adminId) {
        Group group = findGroupByIdOrThrowGroupException(groupId);
        User inviter = findUserByIdOrThrowUserException(adminId);
        User invitee = findUserByEmailOrThrowUserException(email);

        GroupMembership groupMembership = findGroupMemberByIdThrowGroupException(group, inviter);
        if(groupMembership.getRole() != GroupRole.ADMIN){
            throw new GroupException(ExceptionMessage.GROUP_ADMIN_ACCESS_ONLY);
        }

        if (groupMembershipRepository.existsByGroupAndUser(group, invitee)) {
            throw new GroupException(ExceptionMessage.USER_ALREADY_IN_GROUP);
        }

        GroupInvitation invitation = GroupInvitation.builder()
                .group(group)
                .invitee(invitee)
                .inviter(inviter)
                .build();

        GroupInvitation savedInvitation = groupInvitationRepository.save(invitation);

        notificationService.sendNotificationAsync(new NotificationDto("INVITE",
                group.getGroupName() + " 그룹에 초대되셨습니다.",
                inviter.getUserId(),
                invitee.getUserId(),
                savedInvitation.getId()));
    }

    @Transactional
    public void acceptInvitation(Long invitationId, Long userId){
        GroupInvitation invitation = findInvitationByIdOrThrowException(invitationId);
        if(!invitation.getInvitee().getUserId().equals(userId)){
            throw new GroupException(ExceptionMessage.NOT_INVITED_USER);
        }

        invitation.accept();
        groupInvitationRepository.save(invitation);

        GroupMembership membership = GroupMembership.builder()
                .group(invitation.getGroup())
                .user(invitation.getInvitee())
                .role(GroupRole.MEMBER)
                .build();
        groupMembershipRepository.save(membership);

        notificationService.sendNotificationAsync(new NotificationDto("ACCEPT",
                invitation.getInvitee().getUsername() + "님이 그룹에 참여했습니다.",
                invitation.getInvitee().getUserId(),
                invitation.getInviter().getUserId(),
                invitationId));
    }

    @Transactional
    public void rejectInvitation(Long invitationId, Long userId){
        GroupInvitation invitation = findInvitationByIdOrThrowException(invitationId);
        if( !invitation.getInvitee().getUserId().equals(userId)){
            throw new GroupException(ExceptionMessage.NOT_INVITED_USER);
        }

        invitation.reject();
        groupInvitationRepository.save(invitation);

        notificationService.sendNotificationAsync(new NotificationDto("REJECT",
                invitation.getInvitee().getUsername() + "님이 그룹 초대를 거절했습니다.",
                invitation.getInvitee().getUserId(),
                invitation.getInviter().getUserId(),
                invitationId));
    }

    @Transactional
    public void changeGroupMemberRole(Long groupId, Long userId, GroupRole role) {
        Group group = findGroupByIdOrThrowGroupException(groupId);
        User user = findUserByIdOrThrowUserException(userId);
        GroupMembership membership = findGroupMemberByIdThrowGroupException(group, user);

        if (membership.getRole() == GroupRole.ADMIN && role != GroupRole.ADMIN) {
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

    private User findUserByEmailOrThrowUserException(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn(">>>> UserEmail {} : {} <<<<", email, ExceptionMessage.USER_NOT_FOUND);
                    return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });
    }

    private User findUserByIdOrThrowUserException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(">>>> UserId {} : {} <<<<", userId, ExceptionMessage.USER_NOT_FOUND);
                    return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });
    }

    private Group findGroupByIdOrThrowGroupException(Long groupId) {
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

    private GroupInvitation findInvitationByIdOrThrowException(Long invitationId) {
        return groupInvitationRepository.findById(invitationId)
                .orElseThrow(() -> {
                    log.warn(">>>> InvitationId {} : {} <<<<", invitationId, ExceptionMessage.INVITATION_NOT_FOUND);
                    return new GroupException(ExceptionMessage.INVITATION_NOT_FOUND);
                });
    }
}