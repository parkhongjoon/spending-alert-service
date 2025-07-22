package com.zerobase.spendingalertservice.service;

import com.zerobase.spendingalertservice.domain.Card;
import com.zerobase.spendingalertservice.domain.Company;
import com.zerobase.spendingalertservice.repository.CardRepository;
import com.zerobase.spendingalertservice.repository.CompanyRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {

  private final CardRepository cardRepository;
  private final CompanyRepository companyRepository;

  public Card createCard(Long companyId, String name, String cardType,
      Double cashbackRate, Long limitAmount, Long totalAmount, Long annualFee) {

    if (cardRepository.existsByName(name)) {
      throw new RuntimeException("이미 존재하는 카드 상품입니다.");
    }

    Company company = companyRepository.findById(companyId)
        .orElseThrow(() -> new RuntimeException("카드사를 찾을 수 없습니다."));

    Card card = Card.builder()
        .company(company)
        .name(name)
        .cardType(cardType)
        .cashbackRate(cashbackRate)
        .limitAmount(limitAmount)
        .totalAmount(totalAmount)
        .annualFee(annualFee)
        .createdAt(LocalDateTime.now())
        .build();

    return cardRepository.save(card);
  }
}
