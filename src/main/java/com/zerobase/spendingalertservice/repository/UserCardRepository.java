package com.zerobase.spendingalertservice.repository;

import com.zerobase.spendingalertservice.domain.UserCard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCardRepository extends JpaRepository<UserCard, Long> {

  List<UserCard> findByUserId(Long userId);
}
