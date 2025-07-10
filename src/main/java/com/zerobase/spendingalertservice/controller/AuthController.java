package com.zerobase.spendingalertservice.controller;

import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.dto.LoginRequest;
import com.zerobase.spendingalertservice.dto.SignupRequest;
import com.zerobase.spendingalertservice.service.UserService;
import com.zerobase.spendingalertservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

  @PostMapping("/signup")
  public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
    // 관리자인지 회원인지 role도 받도록
    userService.signup(
        request.getName(),
        request.getEmail(),
        request.getPassword(),
        request.getRole()
    );
    return ResponseEntity.ok("회원가입 성공");
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    User user = userService.findByEmail(request.getEmail());
    String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

    return ResponseEntity.ok(token);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/api/admin/only")
  public ResponseEntity<String> adminOnly() {
    return ResponseEntity.ok("관리자만 접근 가능!");
  }

}
