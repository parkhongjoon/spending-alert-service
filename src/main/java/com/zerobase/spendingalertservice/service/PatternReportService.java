package com.zerobase.spendingalertservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.spendingalertservice.domain.PatternReport;
import com.zerobase.spendingalertservice.domain.Transaction;
import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.domain.UserCard;
import com.zerobase.spendingalertservice.repository.PatternReportRepository;
import com.zerobase.spendingalertservice.repository.TransactionRepository;
import com.zerobase.spendingalertservice.repository.UserCardRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatternReportService {

  private final UserCardRepository userCardRepository;
  private final TransactionRepository transactionRepository;
  private final PatternReportRepository patternReportRepository;

  public void savePatternReport(User user, LocalDate start, LocalDate end, String compareGroup) {
    List<UserCard> userCards = userCardRepository.findByUserId(user.getId());

    List<Transaction> transactions = userCards.stream()
        .flatMap(card -> transactionRepository.findByUserCard(card).stream())
        .filter(t -> {
          LocalDate date = t.getTransactionDate().toLocalDate();
          return !date.isBefore(start) && !date.isAfter(end);
        })
        .collect(Collectors.toList());

    long totalSpent = transactions.stream().mapToLong(Transaction::getAmount).sum();

    Map<String, Long> categoryMap = transactions.stream()
        .collect(Collectors.groupingBy(
            Transaction::getCategory,
            Collectors.summingLong(Transaction::getAmount)
        ));

    try {
      String categoryJson = new ObjectMapper().writeValueAsString(categoryMap);

      PatternReport report = PatternReport.builder()
          .user(user)
          .periodStart(start)
          .periodEnd(end)
          .compareGroup(compareGroup)
          .totalSpent(totalSpent)
          .categorySummary(categoryJson)
          .createdAt(LocalDateTime.now())
          .build();

      patternReportRepository.save(report);
    } catch (Exception e) {
      throw new RuntimeException("카테고리 요약 저장 실패", e);
    }
  }

  public List<PatternReport> getReportsByUser(User user) {
    return patternReportRepository.findByUser(user);
  }
}