package com.grepp.nbe1_2_team09.controller.group;

import com.grepp.nbe1_2_team09.admin.service.CustomUserDetails;
import com.grepp.nbe1_2_team09.controller.group.dto.CreateGroupRequest;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupDto;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupMembershipDto;
import com.grepp.nbe1_2_team09.controller.group.dto.UpdateGroupRequest;
import com.grepp.nbe1_2_team09.domain.entity.group.GroupRole;
import com.grepp.nbe1_2_team09.domain.service.group.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;


    //먼저 CRUD 4종 세트 구현 시작
    //jwt 적용시 HttpServletRequest에서 jwtUtil.getUserId(jwtUtil.extractToken(httpRequest))로 userId 추출
    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@Valid @RequestBody CreateGroupRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        GroupDto groupDto = groupService.createGroup(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupDto); // 201 리턴
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> getGroup(@PathVariable Long groupId) {
        GroupDto groupDto = groupService.getGroupById(groupId);
        return ResponseEntity.ok(groupDto);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDto> updateGroup(@PathVariable Long groupId, @Valid @RequestBody UpdateGroupRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        GroupDto groupDto = groupService.updateGroup(groupId, request, userId);
        return ResponseEntity.ok(groupDto);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<GroupDto> deleteGroup(@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        groupService.deleteGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    //userId에 대한 정보를 받아서 해당 사용자가 속한 group들을 보내준다(이때 아마 local storage에 있던게 헤더를 통해서 들어올건지 내일 물어보기
    @GetMapping("/user")
    public ResponseEntity<List<GroupDto>> getUserGroups(@AuthenticationPrincipal CustomUserDetails userDetails) { //userId 변경해야하는 부분
        Long userId = userDetails.getUser().getUserId();
        List<GroupDto> groups = groupService.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }

    //관리자가 그룹에 멤버 추가 시도(초대)
    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMemberToGroup(@PathVariable Long groupId, @RequestParam String email, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long adminId = userDetails.getUser().getUserId();
        groupService.inviteMemberToGroup(groupId, email, adminId);
        return ResponseEntity.ok().build();
    }

    //초대 수락
    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable Long invitationId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        groupService.acceptInvitation(invitationId, userId); // 인자 순서 수정
        return ResponseEntity.ok().build();
    }

    //초대 거절
    @PostMapping("/invitations/{invitationId}/reject")
    public ResponseEntity<Void> rejectInvitation(@PathVariable Long invitationId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        groupService.rejectInvitation(invitationId, userId); // 인자 순서 수정
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}/members/{targetUsername}")
    public ResponseEntity<Void> removeMemberFromGroup(@PathVariable Long groupId, @PathVariable String targetUsername, @AuthenticationPrincipal CustomUserDetails userDetails) {//여기는 다른 사람
        Long userId = userDetails.getUser().getUserId();
        groupService.removeMemberFromGroup(groupId, userId, targetUsername);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMembershipDto>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMembershipDto> groupMembers = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(groupMembers);
    }

    @PutMapping("/{groupId}/members/{targetUsername}/role")
    public ResponseEntity<Void> changeGroupMemberRole(@PathVariable Long groupId, @PathVariable String targetUsername, @RequestParam GroupRole role, @AuthenticationPrincipal CustomUserDetails userDetails) { //userId 변경해야하는 부분
        Long userId = userDetails.getUser().getUserId();
        groupService.changeGroupMemberRole(groupId, targetUsername, role, userId);
        return ResponseEntity.ok().build();
    }


}
