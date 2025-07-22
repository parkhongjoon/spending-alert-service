package com.zerobase.spendingalertservice.controller;

import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.service.NotificationService;
import com.zerobase.spendingalertservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;
  private final UserService userService;


  @Operation(
      security = {@SecurityRequirement(name = "bearer-key")}
  )
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/alert")
  public String sendLimitExceededAlert(
      @RequestParam String userEmail,
      @RequestParam Long totalSpent
  ) {
    //ADMIN 권한 확인
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      System.out.println("권한이 없습니다. 관리자만 알림 발송이 가능합니다.");
      return "권한이 없습니다. 관리자만 알림 발송이 가능합니다.";
    }

    User user = userService.findByEmail(userEmail);
    notificationService.sendLimitExceededAlert(user, totalSpent);

    System.out.println("한도 초과 알림 발송 완료!");
    return "한도 초과 알림 발송 완료!";
  }
}
