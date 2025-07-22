package com.zerobase.spendingalertservice.service;

import com.zerobase.spendingalertservice.domain.Notification;
import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.repository.NotificationRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final NotificationRepository notificationRepository;

  @Value("${mailgun.api.key}")
  private String mailgunApiKey;

  @Value("${mailgun.domain}")
  private String mailgunDomain;

  @Value("${mailgun.from.email}")
  private String mailgunFromEmail;

  public void sendLimitExceededAlert(User user, Long totalSpent) {
    String message = "한도를 초과했습니다! 현재 사용 금액: " + totalSpent;

    Notification notification = Notification.builder()
        .user(user)
        .senderId(1L) // 관리자 ID
        .message(message)
        .sentAt(LocalDateTime.now())
        .channel("EMAIL")
        .status("PENDING")
        .build();

    Notification savedNotification = notificationRepository.save(notification);

    boolean success = sendEmail(user.getEmail(), "한도 초과 알림", message);

    savedNotification.setStatus(success ? "SENT" : "FAILED");
    notificationRepository.save(savedNotification);
  }

  public void sendLimitWarningAlert(User user, Long totalSpent) {
    String message = "한도의 80%를 초과했습니다! 현재 사용 금액: " + totalSpent;

    Notification notification = Notification.builder()
        .user(user)
        .senderId(1L)
        .message(message)
        .sentAt(LocalDateTime.now())
        .channel("EMAIL")
        .status("PENDING")
        .build();

    Notification savedNotification = notificationRepository.save(notification);

    boolean success = sendEmail(user.getEmail(), "한도 80% 초과 경고", message);

    savedNotification.setStatus(success ? "SENT" : "FAILED");
    notificationRepository.save(savedNotification);
  }

  public boolean sendEmail(String to, String subject, String text) {
    try {
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      String auth = "api:" + mailgunApiKey;
      byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
      headers.add("Authorization", "Basic " + new String(encodedAuth));

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("from", mailgunFromEmail);
      body.add("to", to);
      body.add("subject", subject);
      body.add("text", text);

      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

      ResponseEntity<String> response = restTemplate.postForEntity(
          "https://api.mailgun.net/v3/" + mailgunDomain + "/messages",
          request,
          String.class
      );

      log.info("Mailgun response: {}", response.getBody());
      return response.getStatusCode() == HttpStatus.OK;
    } catch (Exception e) {
      log.error("Mailgun send failed: {}", e.getMessage());
      return false;
    }
  }

  public void sendCompareWithLastMonthAlert(User user, long thisMonthTotal, long lastMonthTotal) {
    String message = String.format(
        "이번 달 소비가 지난달보다 많습니다.\n지난달: %,d원 / 이번달: %,d원", lastMonthTotal, thisMonthTotal
    );

    Notification notification = Notification.builder()
        .user(user)
        .senderId(1L)
        .message(message)
        .sentAt(LocalDateTime.now())
        .channel("EMAIL")
        .status("PENDING")
        .build();

    Notification savedNotification = notificationRepository.save(notification);

    boolean success = sendEmail(user.getEmail(), "소비 증가 알림", message);

    savedNotification.setStatus(success ? "SENT" : "FAILED");
    notificationRepository.save(savedNotification);
  }

}
