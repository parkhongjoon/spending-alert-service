package com.zerobase.spendingalertservice.dto;

import com.zerobase.spendingalertservice.domain.Transaction;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TransactionResponse {

  private Long id;
  private Long userCardId;
  private String merchant;
  private Integer amount;
  private String category;
  private LocalDateTime transactionDate;
  private LocalDateTime createdAt;

  public TransactionResponse(Transaction transaction) {
    this.id = transaction.getId();
    this.userCardId = transaction.getUserCard().getId();
    this.merchant = transaction.getMerchant();
    this.amount = transaction.getAmount();
    this.category = transaction.getCategory();
    this.transactionDate = transaction.getTransactionDate();
    this.createdAt = transaction.getCreatedAt();
  }
}
