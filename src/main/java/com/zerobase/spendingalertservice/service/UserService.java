package com.zerobase.spendingalertservice.service;

import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User signup(String name, String email, String password, User.Role role) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new RuntimeException("이미 가입된 이메일입니다.");
    }

    User user = User.builder()
        .name(name)
        .email(email)
        .password(passwordEncoder.encode(password))
        .role(role)
        .createdAt(LocalDateTime.now())
        .build();

    return userRepository.save(user);
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .roles(user.getRole().name()) // ROLE_ 접두어 자동 처리
        .build();
  }

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    System.out.println(">> JWT sub(email): " + email);
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("현재 로그인한 사용자를 찾을 수 없습니다."));
  }

  public void setGoalAmount(String email, Long amount) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("사용자 없음"));
    user.setGoalAmount(amount);
    userRepository.save(user);
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("해당 이메일의 유저를 찾을 수 없습니다."));
  }

  public void setLimitAmount(Long amount) {
    User user = getCurrentUser();
    user.setLimitAmount(amount);
    userRepository.save(user);
  }


}
