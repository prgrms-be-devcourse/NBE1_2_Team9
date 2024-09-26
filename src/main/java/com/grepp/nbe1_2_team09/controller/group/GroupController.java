package com.grepp.nbe1_2_team09.controller.group;

import com.grepp.nbe1_2_team09.controller.group.dto.CreateGroupRequest;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupDto;
import com.grepp.nbe1_2_team09.controller.group.dto.GroupMembershipDto;
import com.grepp.nbe1_2_team09.controller.group.dto.UpdateGroupRequest;
import com.grepp.nbe1_2_team09.domain.entity.Role;
import com.grepp.nbe1_2_team09.domain.service.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;


    //먼저 CRUD 4종 세트 구현 시작

    public ResponseEntity<GroupDto> createGroup(CreateGroupRequest request, Long userId) {
        GroupDto groupDto = groupService.createGroup(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupDto); // 201 리턴
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> getGroup(@PathVariable Long groupId) {
        GroupDto groupDto = groupService.getGroupById(groupId);
        return ResponseEntity.ok(groupDto);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDto> updateGroup(@PathVariable Long groupId, UpdateGroupRequest request) {
        GroupDto groupDto = groupService.updateGroup(groupId, request);
        return ResponseEntity.ok(groupDto);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<GroupDto> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.noContent().build();
    }

    //userId에 대한 정보를 받아서 해당 사용자가 속한 group들을 보내준다(이때 아마 local storage에 있던게 헤더를 통해서 들어올건지 내일 물어보기
    @GetMapping("/user")
    public ResponseEntity<List<GroupDto>> getUserGroups(Long userId){ //userId 변경해야하는 부분
        List<GroupDto> groups = groupService.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }

    //관리자가 그룹에 멤버 추가 -> Role은 받을 필요 없음 그냥 처음에는 무조건 MEMBER로 하도록 변경할것 내일 -> 변경 완료
    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMemberToGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        groupService.addMemberToGroup(groupId,userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMemberFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {//여기는 다른 사람
        groupService.removeMemberFromGroup(groupId,userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMembershipDto>> getGroupMembers(@PathVariable Long groupId){
        List<GroupMembershipDto> groupMembers = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(groupMembers);
    }

    @PutMapping("/{groupId}/members/{userId}/role")
    public ResponseEntity<Void> changeGroupMemberRole(@PathVariable Long groupId, @PathVariable Long userId, @RequestParam Role role) { //userId 변경해야하는 부분
        groupService.changeGroupMemberRole(groupId,userId,role);
        return ResponseEntity.ok().build();
    }
}
