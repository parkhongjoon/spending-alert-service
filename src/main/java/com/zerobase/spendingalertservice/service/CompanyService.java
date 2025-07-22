package com.zerobase.spendingalertservice.service;

import com.zerobase.spendingalertservice.domain.Company;
import com.zerobase.spendingalertservice.repository.CompanyRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

  private final CompanyRepository companyRepository;

  public Company createCompany(String name, String address, String contactEmail) {
    if (companyRepository.existsByName(name)) {
      throw new RuntimeException("이미 존재하는 카드사입니다.");
    }

    Company company = Company.builder()
        .name(name)
        .address(address)
        .contactEmail(contactEmail)
        .createdAt(LocalDateTime.now())
        .build();

    return companyRepository.save(company);
  }
}
