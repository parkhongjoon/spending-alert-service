package com.zerobase.spendingalertservice.controller;

import com.zerobase.spendingalertservice.domain.Card;
import com.zerobase.spendingalertservice.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

  private final CardService cardService;

  @PreAuthorize("hasRole('ADMIN')") // 관리자만 허용
  @PostMapping
  public ResponseEntity<Card> createCard(
      @RequestParam Long companyId,
      @RequestParam String name,
      @RequestParam String cardType,
      @RequestParam Double cashbackRate,
      @RequestParam Long limitAmount,
      @RequestParam Long totalAmount,
      @RequestParam Long annualFee
  ) {
    Card card = cardService.createCard(companyId, name, cardType, cashbackRate, limitAmount,
        totalAmount, annualFee);
    return ResponseEntity.ok(card);
  }
}
