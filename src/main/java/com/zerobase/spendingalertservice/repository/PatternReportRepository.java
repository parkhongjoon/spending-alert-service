package com.zerobase.spendingalertservice.repository;

import com.zerobase.spendingalertservice.domain.PatternReport;
import com.zerobase.spendingalertservice.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatternReportRepository extends JpaRepository<PatternReport, Long> {

  List<PatternReport> findByUser(User user);
}

