package com.zerobase.spendingalertservice.controller;

import com.zerobase.spendingalertservice.domain.Transaction;
import com.zerobase.spendingalertservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping
  public ResponseEntity<Transaction> createTransaction(@RequestParam String merchant,
      @RequestParam Integer amount,
      @RequestParam String category) {
    Transaction transaction = transactionService.createTransaction(merchant, amount, category);
    return ResponseEntity.ok(transaction);
  }
}
