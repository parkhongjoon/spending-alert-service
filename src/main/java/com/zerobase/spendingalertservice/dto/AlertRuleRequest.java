package com.zerobase.spendingalertservice.dto;

import lombok.Data;

@Data
public class AlertRuleRequest {

  private String conditionType;
  private Long thresholdValue;
  private String channel;
}

