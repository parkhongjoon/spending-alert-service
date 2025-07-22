package com.zerobase.spendingalertservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategorySpendingResponse {

  private String category;
  private long totalSpent;
}
