package com.zerobase.spendingalertservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CashbackResponse {

  private String cardName;
  private Long totalCashback;
}

