package com.zerobase.spendingalertservice.scheduler;

import com.zerobase.spendingalertservice.domain.AlertRule;
import com.zerobase.spendingalertservice.domain.ConditionType;
import com.zerobase.spendingalertservice.domain.Transaction;
import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.domain.UserCard;
import com.zerobase.spendingalertservice.repository.AlertRuleRepository;
import com.zerobase.spendingalertservice.repository.TransactionRepository;
import com.zerobase.spendingalertservice.repository.UserCardRepository;
import com.zerobase.spendingalertservice.repository.UserRepository;
import com.zerobase.spendingalertservice.service.NotificationService;
import com.zerobase.spendingalertservice.service.PatternReportService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LimitScheduler {

  private final UserCardRepository userCardRepository;
  private final TransactionRepository transactionRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;
  private final AlertRuleRepository alertRuleRepository;
  private final PatternReportService patternReportService;


  //@Scheduled(cron = "0 0 * * * *") // 매시 정각
  @Scheduled(fixedDelay = 5000) // 5초마다 실행
  public void checkUserLimits() {
    log.info("한도초과 예측시작");

    var users = userRepository.findAll();

    for (User user : users) {
      if (user.getLimitAmount() == null) {
        continue;
      }

      // 사용자의 모든 발급카드 조회
      var userCards = userCardRepository.findByUserId(user.getId());

      // 모든 카드 거래내역 누적 합산
      long totalSpent = userCards.stream()
          .flatMap(uc -> transactionRepository.findByUserCard(uc).stream())
          .mapToLong(Transaction::getAmount)
          .sum();

      if (totalSpent >= user.getLimitAmount()) {
        notificationService.sendLimitExceededAlert(user, totalSpent);
      } else if (totalSpent >= user.getLimitAmount() * 0.8) {
        notificationService.sendLimitWarningAlert(user, totalSpent);
      }
    }
  }

  @Scheduled(cron = "0 0 10 * * ?")  // 매일 오전 10시
  public void checkLastMonthCompareAlerts() {
    log.info("🔔 지난달 대비 증가 점검 시작");

    List<AlertRule> rules = alertRuleRepository.findByConditionType(
        ConditionType.valueOf("LAST_MONTH_COMPARE"));

    for (AlertRule rule : rules) {
      User user = rule.getUser();
      List<UserCard> userCards = userCardRepository.findByUserId(user.getId());

      LocalDateTime now = LocalDateTime.now();
      LocalDateTime startOfThisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
          .withNano(0);
      LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);
      LocalDateTime endOfLastMonth = startOfThisMonth.minusNanos(1);

      long lastMonthTotal = userCards.stream()
          .flatMap(uc -> transactionRepository.findByUserCard(uc).stream())
          .filter(t -> !t.getTransactionDate().isBefore(startOfLastMonth) && !t.getTransactionDate()
              .isAfter(endOfLastMonth))
          .mapToLong(Transaction::getAmount)
          .sum();

      long thisMonthTotal = userCards.stream()
          .flatMap(uc -> transactionRepository.findByUserCard(uc).stream())
          .filter(t -> !t.getTransactionDate().isBefore(startOfThisMonth))
          .mapToLong(Transaction::getAmount)
          .sum();

      if (thisMonthTotal > lastMonthTotal) {
        notificationService.sendCompareWithLastMonthAlert(user, thisMonthTotal, lastMonthTotal);
      }
      patternReportService.savePatternReport(
          user,
          startOfLastMonth.toLocalDate(),
          endOfLastMonth.toLocalDate(),
          "지난달"
      );

      patternReportService.savePatternReport(
          user,
          startOfThisMonth.toLocalDate(),
          now.toLocalDate(),
          "이번달"
      );
    }
  }

}

