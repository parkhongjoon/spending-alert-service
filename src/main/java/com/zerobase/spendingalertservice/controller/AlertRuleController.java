package com.zerobase.spendingalertservice.controller;

import com.zerobase.spendingalertservice.domain.AlertRule;
import com.zerobase.spendingalertservice.dto.AlertRuleRequest;
import com.zerobase.spendingalertservice.service.AlertRuleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertRuleController {

  private final AlertRuleService alertRuleService;

  @PostMapping
  public ResponseEntity<String> createAlertRule(@RequestBody AlertRuleRequest request) {
    alertRuleService.createAlertRule(request);
    return ResponseEntity.ok("알림 조건이 설정되었습니다.");
  }

  @GetMapping
  public ResponseEntity<List<AlertRule>> getMyAlertRules() {
    return ResponseEntity.ok(alertRuleService.getMyAlertRules());
  }
}

