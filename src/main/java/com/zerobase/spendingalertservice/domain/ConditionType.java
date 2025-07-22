package com.zerobase.spendingalertservice.domain;

public enum ConditionType {
  LIMIT_PERCENT,         // 사용 한도 80% 초과
  LIMIT_EXCEEDED,        // 사용 한도 초과
  LAST_MONTH_COMPARE     // 지난달보다 소비 증가
}