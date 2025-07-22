package com.zerobase.spendingalertservice.dto;

import com.zerobase.spendingalertservice.domain.User;
import lombok.Data;

@Data
public class SignupRequest {

  private String name;
  private String email;
  private String password;
  private User.Role role; // ROLE_USER, ROLE_ADMIN
}
