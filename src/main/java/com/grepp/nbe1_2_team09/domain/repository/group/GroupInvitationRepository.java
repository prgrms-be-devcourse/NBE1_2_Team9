package com.grepp.nbe1_2_team09.domain.repository.group;

import com.grepp.nbe1_2_team09.domain.entity.group.invitation.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

}
