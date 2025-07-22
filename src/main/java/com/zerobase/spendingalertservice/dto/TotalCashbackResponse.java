package com.zerobase.spendingalertservice.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalCashbackResponse {

  private Long totalCashback;
  private List<CashbackResponse> cards;
}