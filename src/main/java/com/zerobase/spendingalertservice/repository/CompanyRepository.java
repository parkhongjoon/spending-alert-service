package com.zerobase.spendingalertservice.repository;

import com.zerobase.spendingalertservice.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

  boolean existsByName(String name);
}
