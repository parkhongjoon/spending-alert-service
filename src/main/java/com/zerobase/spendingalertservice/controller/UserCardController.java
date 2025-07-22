package com.zerobase.spendingalertservice.controller;

import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.domain.UserCard;
import com.zerobase.spendingalertservice.dto.UserCardResponse;
import com.zerobase.spendingalertservice.service.UserCardService;
import com.zerobase.spendingalertservice.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-cards")
@RequiredArgsConstructor
public class UserCardController {

  private final UserCardService userCardService;
  private final UserService userService;

  // 발급
  @PostMapping("/issue")
  public ResponseEntity<UserCardResponse> issueCard(
      @RequestParam Long cardId) {

    User user = userService.getCurrentUser();
    UserCard userCard = userCardService.issueCard(user.getEmail(), cardId);

    return ResponseEntity.ok(new UserCardResponse(userCard));
  }

  // 조회
  @GetMapping("/my")
  public ResponseEntity<List<UserCardResponse>> getMyUserCards() {
    User user = userService.getCurrentUser();
    List<UserCard> userCards = userCardService.getUserCardsByUserId(user.getId());

    List<UserCardResponse> response = userCards.stream()
        .map(UserCardResponse::new)
        .toList();

    return ResponseEntity.ok(response);
  }
}
