package com.zerobase.spendingalertservice.repository;

import com.zerobase.spendingalertservice.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

  boolean existsByName(String name);
}
