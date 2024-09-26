package com.grepp.nbe1_2_team09.domain.repository.finance;

import com.grepp.nbe1_2_team09.controller.finance.dto.AccountBookResp;
import com.grepp.nbe1_2_team09.domain.entity.Expense;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBookRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByGroup_GroupId(Long groupId);
}
