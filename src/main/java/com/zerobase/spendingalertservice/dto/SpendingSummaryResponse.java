package com.zerobase.spendingalertservice.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpendingSummaryResponse {

  private Long totalSpent;                    // 총합 소비액
  private Map<String, Long> categorySummary;  // 카테고리별 소비합계
}
