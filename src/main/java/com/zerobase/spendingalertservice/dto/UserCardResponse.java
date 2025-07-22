package com.zerobase.spendingalertservice.dto;

import com.zerobase.spendingalertservice.domain.UserCard;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserCardResponse {

  private Long id;
  private Long cardId;
  private String cardName;
  private String companyName;
  private Double cashbackRate;
  private Long limitAmount;
  private Long totalAmount;
  private LocalDateTime issuedAt;
  private String status;
  private Long totalCashback;

  public UserCardResponse(UserCard userCard) {
    this.id = userCard.getId();
    this.cardId = userCard.getCard().getId();
    this.cardName = userCard.getCard().getName();
    this.companyName = userCard.getCard().getCompany().getName();
    this.cashbackRate = userCard.getCard().getCashbackRate();
    this.limitAmount = userCard.getCard().getLimitAmount();
    this.totalAmount = userCard.getCard().getTotalAmount();
    this.issuedAt = userCard.getIssuedAt();
    this.status = userCard.getStatus();
    this.totalCashback = userCard.getTotalCashback();
  }

}
