package com.zerobase.spendingalertservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private User user;

  private LocalDateTime transactionDate;

  private String merchant;

  private Integer amount;

  private String category;

  private LocalDateTime createdAt;
}
