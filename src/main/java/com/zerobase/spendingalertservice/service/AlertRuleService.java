package com.zerobase.spendingalertservice.service;

import com.zerobase.spendingalertservice.domain.AlertRule;
import com.zerobase.spendingalertservice.domain.ConditionType;
import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.dto.AlertRuleRequest;
import com.zerobase.spendingalertservice.repository.AlertRuleRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertRuleService {

  private final AlertRuleRepository alertRuleRepository;
  private final UserService userService;

  public void createAlertRule(AlertRuleRequest request) {
    User user = userService.getCurrentUser();

    AlertRule rule = AlertRule.builder()
        .user(user)
        .conditionType(ConditionType.valueOf(request.getConditionType()))
        .thresholdValue(request.getThresholdValue())
        .channel(request.getChannel())
        .createdAt(LocalDateTime.now())
        .build();

    alertRuleRepository.save(rule);
  }

  public List<AlertRule> getMyAlertRules() {
    User user = userService.getCurrentUser();
    return alertRuleRepository.findByUserId(user.getId());
  }
}
