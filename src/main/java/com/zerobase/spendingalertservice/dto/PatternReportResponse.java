package com.zerobase.spendingalertservice.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.spendingalertservice.domain.PatternReport;
import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatternReportResponse {

  private String compareGroup;
  private LocalDate periodStart;
  private LocalDate periodEnd;
  private long totalSpent;
  private Map<String, Long> categorySummary;

  public static PatternReportResponse from(PatternReport report) {
    try {
      Map<String, Long> summary = new ObjectMapper()
          .readValue(report.getCategorySummary(), new TypeReference<>() {
          });
      return new PatternReportResponse(
          report.getCompareGroup(),
          report.getPeriodStart(),
          report.getPeriodEnd(),
          report.getTotalSpent(),
          summary
      );
    } catch (Exception e) {
      throw new RuntimeException("카테고리 요약 실패", e);
    }
  }
}
