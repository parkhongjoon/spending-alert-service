package com.zerobase.spendingalertservice.service;

import com.zerobase.spendingalertservice.domain.Card;
import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.domain.UserCard;
import com.zerobase.spendingalertservice.repository.CardRepository;
import com.zerobase.spendingalertservice.repository.UserCardRepository;
import com.zerobase.spendingalertservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCardService {

  private final UserRepository userRepository;
  private final CardRepository cardRepository;
  private final UserCardRepository userCardRepository;

  public UserCard issueCard(String email, Long cardId) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    Card card = cardRepository.findById(cardId)
        .orElseThrow(() -> new RuntimeException("카드를 찾을 수 없습니다."));

    UserCard userCard = UserCard.builder()
        .user(user)
        .card(card)
        .cardName(card.getName())
        .companyName(card.getCompany().getName())
        .cardType(card.getCardType())
        .limitAmount(card.getLimitAmount())
        .totalAmount(card.getTotalAmount())
        .issuedAt(LocalDateTime.now())
        .status("ACTIVE")
        .build();

    return userCardRepository.save(userCard);
  }

  public List<UserCard> getUserCardsByUserId(Long userId) {
    return userCardRepository.findByUserId(userId);
  }

}
