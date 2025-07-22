package com.zerobase.spendingalertservice.service;

import com.zerobase.spendingalertservice.domain.Transaction;
import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.domain.UserCard;
import com.zerobase.spendingalertservice.dto.SpendingSummaryResponse;
import com.zerobase.spendingalertservice.repository.TransactionRepository;
import com.zerobase.spendingalertservice.repository.UserCardRepository;
import com.zerobase.spendingalertservice.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final UserCardRepository userCardRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  public Transaction createTransaction(Long userCardId, String merchant, Integer amount,
      String category) {
    UserCard userCard = userCardRepository.findById(userCardId)
        .orElseThrow(() -> new RuntimeException("발급 카드를 찾을 수 없습니다."));

    User user = userCard.getUser();
    var card = userCard.getCard();

    // 1회 결제 한도 초과 여부 확인
    if (card.getLimitAmount() != null && amount > card.getLimitAmount()) {
      throw new RuntimeException("1회 결제 한도를 초과하여 거래가 차단되었습니다.");
    }

    // 해당 카드의 누적 거래 금액 확인
    int cardSpent = transactionRepository.findByUserCard(userCard).stream()
        .mapToInt(Transaction::getAmount)
        .sum();

    if (card.getTotalAmount() != null && (cardSpent + amount) > card.getTotalAmount()) {
      throw new RuntimeException("카드의 총 사용 한도를 초과하여 거래가 차단되었습니다.");
    }

    // 사용자 전체 지출 계산
    int totalSpent = userCardRepository.findByUserId(user.getId()).stream()
        .flatMap(uc -> transactionRepository.findByUserCard(uc).stream())
        .mapToInt(Transaction::getAmount)
        .sum();

    int expectedTotal = totalSpent + amount;

    if (user.getLimitAmount() != null) {
      long limitAmount = user.getLimitAmount();

      if (expectedTotal > limitAmount) {
        throw new RuntimeException("사용자 한도 초과로 인해 거래가 차단되었습니다.");
      } else if (expectedTotal == limitAmount) {
        notificationService.sendLimitExceededAlert(user, (long) expectedTotal);
      } else if (expectedTotal >= limitAmount * 0.8) {
        notificationService.sendLimitWarningAlert(user, (long) expectedTotal);
      }
    }

    // 캐시백 계산
    Long currentCashback = userCard.getTotalCashback() != null ? userCard.getTotalCashback() : 0L;
    long cashback = Math.round(amount * userCard.getCard().getCashbackRate());
    userCard.setTotalCashback(currentCashback + cashback);
    userCardRepository.save(userCard);

    // 캐시백 누적
    Long currentUserCashback = user.getTotalCashback() != null ? user.getTotalCashback() : 0L;
    user.setTotalCashback(currentUserCashback + cashback);
    userRepository.save(user);

    // 거래저장
    Transaction transaction = Transaction.builder()
        .userCard(userCard)
        .transactionDate(LocalDateTime.now())
        .merchant(merchant)
        .amount(amount)
        .category(category)
        .createdAt(LocalDateTime.now())
        .build();

    return transactionRepository.save(transaction);
  }

  // 전체 거래내역 조회
  public List<Transaction> getTransactions(String email, Long cardId, LocalDate startDate,
      LocalDate endDate, String category) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

    List<UserCard> userCards = userCardRepository.findByUserId(user.getId());

    // 카드 소유자 검증
    if (cardId != null) {
      boolean ownsCard = userCards.stream()
          .anyMatch(uc -> uc.getId().equals(cardId));

      if (!ownsCard) {
        throw new AccessDeniedException("해당 카드는 현재 사용자 소유가 아닙니다.");
      }

      // 해당 카드만 필터링
      userCards = userCards.stream()
          .filter(uc -> uc.getId().equals(cardId))
          .toList();
    }

    return userCards.stream()
        .flatMap(uc -> transactionRepository.findByUserCard(uc).stream())
        .filter(tx -> {
          boolean match = true;
          if (startDate != null) {
            match &= !tx.getTransactionDate().toLocalDate().isBefore(startDate);
          }
          if (endDate != null) {
            match &= !tx.getTransactionDate().toLocalDate().isAfter(endDate);
          }
          if (category != null) {
            match &= tx.getCategory().equalsIgnoreCase(category);
          }
          return match;
        })
        .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
        .collect(Collectors.toList());
  }

  public SpendingSummaryResponse getSpendingSummary(String email, LocalDate startDate,
      LocalDate endDate) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

    List<UserCard> userCards = userCardRepository.findByUserId(user.getId());

    List<Transaction> transactions = userCards.stream()
        .flatMap(uc -> transactionRepository.findByUserCard(uc).stream())
        .filter(tx -> {
          LocalDate date = tx.getTransactionDate().toLocalDate();
          boolean match = true;
          if (startDate != null) {
            match &= !date.isBefore(startDate);
          }
          if (endDate != null) {
            match &= !date.isAfter(endDate);
          }
          return match;
        })
        .toList();

    long totalSpent = transactions.stream()
        .mapToLong(Transaction::getAmount)
        .sum();

    Map<String, Long> categorySummary = transactions.stream()
        .collect(Collectors.groupingBy(
            Transaction::getCategory,
            Collectors.summingLong(Transaction::getAmount)
        ));

    return new SpendingSummaryResponse(totalSpent, categorySummary);
  }


}
