package com.zerobase.spendingalertservice.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TransactionRequest {

  private LocalDateTime transactionDate;
  private String merchant;
  private Long amount;
  private String category;
}
