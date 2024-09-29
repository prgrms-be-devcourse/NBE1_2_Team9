package com.grepp.nbe1_2_team09.domain.repository.group;

import com.grepp.nbe1_2_team09.domain.entity.User;
import com.grepp.nbe1_2_team09.domain.entity.group.invitation.GroupInvitation;
import com.grepp.nbe1_2_team09.domain.entity.group.invitation.InvitationStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupInvitationRepository extends CrudRepository<GroupInvitation, Long> {
    List<GroupInvitation> findByInviteeAndStatus(User invitee, InvitationStatus status);
}
