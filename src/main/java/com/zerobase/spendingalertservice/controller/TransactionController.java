package com.zerobase.spendingalertservice.controller;

import com.zerobase.spendingalertservice.domain.Transaction;
import com.zerobase.spendingalertservice.dto.SpendingSummaryResponse;
import com.zerobase.spendingalertservice.dto.TransactionResponse;
import com.zerobase.spendingalertservice.service.TransactionService;
import com.zerobase.spendingalertservice.util.JwtUtil;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;
  private final JwtUtil jwtUtil;

  @PostMapping
  public ResponseEntity<TransactionResponse> createTransaction(
      @RequestParam Long userCardId,
      @RequestParam String merchant,
      @RequestParam Integer amount,
      @RequestParam String category) {

    Transaction transaction = transactionService.createTransaction(
        userCardId, merchant, amount, category);

    return ResponseEntity.ok(new TransactionResponse(transaction));
  }

  @GetMapping
  public ResponseEntity<List<TransactionResponse>> getTransactions(
      @RequestHeader("Authorization") String token,
      @RequestParam(required = false) Long cardId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) String category
  ) {
    String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
    List<Transaction> transactions = transactionService.getTransactions(email, cardId, startDate,
        endDate, category);

    List<TransactionResponse> response = transactions.stream()
        .map(TransactionResponse::new)
        .toList();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/summary")
  public ResponseEntity<SpendingSummaryResponse> getSpendingSummary(
      @RequestHeader("Authorization") String token,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
  ) {
    String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
    SpendingSummaryResponse summary = transactionService.getSpendingSummary(email, startDate,
        endDate);
    return ResponseEntity.ok(summary);
  }


}
