package com.zerobase.spendingalertservice.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalSpendingResponse {

  private long totalSpent;
  private List<CardSpendingResponse> cardSpendings;
  private List<CategorySpendingResponse> categorySpendings;
}
