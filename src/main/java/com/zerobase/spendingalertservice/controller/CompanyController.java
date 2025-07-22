package com.zerobase.spendingalertservice.controller;

import com.zerobase.spendingalertservice.domain.Company;
import com.zerobase.spendingalertservice.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

  private final CompanyService companyService;

  @PreAuthorize("hasRole('ADMIN')") //  관리자만 허용
  @PostMapping
  public ResponseEntity<Company> createCompany(
      @RequestParam String name,
      @RequestParam String address,
      @RequestParam String contactEmail) {

    Company company = companyService.createCompany(name, address, contactEmail);
    return ResponseEntity.ok(company);
  }
}
