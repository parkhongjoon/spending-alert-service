package com.zerobase.spendingalertservice.repository;

import com.zerobase.spendingalertservice.domain.Transaction;
import com.zerobase.spendingalertservice.domain.UserCard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findByUserCard(UserCard userCard);

}
