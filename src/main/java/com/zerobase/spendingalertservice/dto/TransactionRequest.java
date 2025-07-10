package com.zerobase.spendingalertservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionRequest {
  private LocalDateTime transactionDate;
  private String merchant;
  private Long amount;
  private String category;
}
