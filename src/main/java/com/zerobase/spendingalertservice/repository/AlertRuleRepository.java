package com.zerobase.spendingalertservice.repository;

import com.zerobase.spendingalertservice.domain.AlertRule;
import com.zerobase.spendingalertservice.domain.ConditionType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

  List<AlertRule> findByUserId(Long userId);

  List<AlertRule> findByConditionType(ConditionType conditionType);
}
