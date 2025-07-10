package com.zerobase.spendingalertservice.repository;

import com.zerobase.spendingalertservice.domain.Transaction;
import com.zerobase.spendingalertservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findByUser(User user);
}
