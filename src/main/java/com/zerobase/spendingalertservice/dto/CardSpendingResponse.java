package com.zerobase.spendingalertservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardSpendingResponse {

  private String cardName;
  private long totalSpent;
}
