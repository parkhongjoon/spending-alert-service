package com.zerobase.spendingalertservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  public enum Role {
    USER, ADMIN
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(unique = true)
  private String email;

  private String password;

  private Long limitAmount;

  @Enumerated(EnumType.STRING)
  private Role role;

  private LocalDateTime createdAt;
}
