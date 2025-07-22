package com.zerobase.spendingalertservice.controller;

import com.zerobase.spendingalertservice.domain.PatternReport;
import com.zerobase.spendingalertservice.domain.Transaction;
import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.domain.UserCard;
import com.zerobase.spendingalertservice.dto.CardSpendingResponse;
import com.zerobase.spendingalertservice.dto.CashbackResponse;
import com.zerobase.spendingalertservice.dto.CategorySpendingResponse;
import com.zerobase.spendingalertservice.dto.PatternReportResponse;
import com.zerobase.spendingalertservice.dto.TotalCashbackResponse;
import com.zerobase.spendingalertservice.dto.TotalSpendingResponse;
import com.zerobase.spendingalertservice.repository.TransactionRepository;
import com.zerobase.spendingalertservice.repository.UserCardRepository;
import com.zerobase.spendingalertservice.service.PatternReportService;
import com.zerobase.spendingalertservice.service.UserService;
import com.zerobase.spendingalertservice.util.JwtUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

public class UserController {

  private final UserService userService;
  private final JwtUtil jwtUtil;
  private final UserCardRepository userCardRepository;
  private final TransactionRepository transactionRepository;
  private final PatternReportService patternReportService;

  @PutMapping("/goal")
  public ResponseEntity<String> setGoalAmount(
      @RequestParam Long amount,
      @RequestHeader("Authorization") String token
  ) {
    String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
    userService.setGoalAmount(email, amount);
    return ResponseEntity.ok("목표 금액 설정 완료");
  }

  @GetMapping("/goal/remaining")
  public ResponseEntity<Long> getRemainingBudget(
      @RequestHeader("Authorization") String token
  ) {
    String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
    User user = userService.getUserByEmail(email);

    List<UserCard> userCards = userCardRepository.findByUserId(user.getId());
    long totalSpent = userCards.stream()
        .flatMap(card -> transactionRepository.findByUserCard(card).stream())
        .mapToLong(Transaction::getAmount)
        .sum();

    long remaining = user.getGoalAmount() - totalSpent;
    return ResponseEntity.ok(remaining);
  }

  @PutMapping("/limit")
  public ResponseEntity<String> setLimitAmount(
      @RequestParam Long amount
  ) {
    userService.setLimitAmount(amount);
    return ResponseEntity.ok("한도 금액 설정 완료");
  }

  @GetMapping("/cashback")
  public ResponseEntity<TotalCashbackResponse> getTotalCashbackReport() {
    User user = userService.getCurrentUser();
    List<UserCard> userCards = userCardRepository.findByUserId(user.getId());

    List<CashbackResponse> cards = userCards.stream()
        .map(uc -> new CashbackResponse(
            uc.getCard().getName(),
            uc.getTotalCashback() != null ? uc.getTotalCashback() : 0L
        ))
        .collect(Collectors.toList());

    long total = cards.stream().mapToLong(CashbackResponse::getTotalCashback).sum();

    return ResponseEntity.ok(new TotalCashbackResponse(total, cards));
  }

  @GetMapping("/spending")
  public ResponseEntity<TotalSpendingResponse> getSpendingReport(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    User user = userService.getCurrentUser();
    List<UserCard> userCards = userCardRepository.findByUserId(user.getId());

    LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : LocalDateTime.MIN;
    LocalDateTime toDateTime = to != null ? to.atTime(23, 59, 59) : LocalDateTime.now();

    // 카드별 소비 계산
    List<CardSpendingResponse> cardSpendings = userCards.stream()
        .map(uc -> {
          long spent = transactionRepository.findByUserCard(uc).stream()
              .filter(t -> !t.getTransactionDate().isBefore(fromDateTime) && !t.getTransactionDate()
                  .isAfter(toDateTime))
              .mapToLong(Transaction::getAmount)
              .sum();
          return new CardSpendingResponse(uc.getCard().getName(), spent);
        })
        .collect(Collectors.toList());

    long totalSpent = cardSpendings.stream()
        .mapToLong(CardSpendingResponse::getTotalSpent)
        .sum();

    // 카테고리별 소비
    Map<String, Long> categoryMap = userCards.stream()
        .flatMap(uc -> transactionRepository.findByUserCard(uc).stream())
        .filter(t -> !t.getTransactionDate().isBefore(fromDateTime) && !t.getTransactionDate()
            .isAfter(toDateTime))
        .collect(Collectors.groupingBy(
            Transaction::getCategory,
            Collectors.summingLong(Transaction::getAmount)
        ));

    List<CategorySpendingResponse> categorySpendings = categoryMap.entrySet().stream()
        .map(e -> new CategorySpendingResponse(e.getKey(), e.getValue()))
        .collect(Collectors.toList());

    return ResponseEntity.ok(
        new TotalSpendingResponse(totalSpent, cardSpendings, categorySpendings));
  }

  @PostMapping("/report/save")
  public ResponseEntity<String> saveReport(
      @RequestParam String type, // 주간, 월간
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    User user = userService.getCurrentUser();
    patternReportService.savePatternReport(user, from, to, type.toUpperCase());
    return ResponseEntity.ok("소비 리포트 저장 완료");
  }

  @GetMapping("/report")
  public ResponseEntity<List<PatternReportResponse>> getReports() {
    User user = userService.getCurrentUser();
    List<PatternReport> reports = patternReportService.getReportsByUser(user);
    return ResponseEntity.ok(
        reports.stream().map(PatternReportResponse::from).toList()
    );
  }

}

