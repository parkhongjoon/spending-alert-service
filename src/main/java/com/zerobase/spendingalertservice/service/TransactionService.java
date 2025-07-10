package com.zerobase.spendingalertservice.service;

import com.zerobase.spendingalertservice.domain.Transaction;
import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final UserService userService;
  private final NotificationService notificationService;

  public Transaction createTransaction(String merchant, Integer amount, String category) {
    User user = userService.getCurrentUser();

    Transaction transaction = Transaction.builder()
        .user(user)
        .transactionDate(LocalDateTime.now())
        .merchant(merchant)
        .amount(amount)
        .category(category)
        .createdAt(LocalDateTime.now())
        .build();

    transactionRepository.save(transaction);

    // 총 지출 계산
    Integer totalSpent = transactionRepository.findByUser(user)
        .stream()
        .mapToInt(Transaction::getAmount)
        .sum();

    if (user.getLimitAmount() != null) {
      long limitAmount = user.getLimitAmount();

      // 한도 초과
      if (totalSpent >= limitAmount) {
        notificationService.sendLimitExceededAlert(user, totalSpent.longValue());
        // TODO: Mailgun 연동
      }
      // 80% 초과 경고
      else if (totalSpent >= (limitAmount * 0.8)) {
        notificationService.sendLimitWarningAlert(user, totalSpent.longValue());
        // TODO: Mailgun 연동
      }
    }

    return transaction;
  }
}
