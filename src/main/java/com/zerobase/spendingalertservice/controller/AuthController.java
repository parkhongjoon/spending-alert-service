package com.zerobase.spendingalertservice.controller;

import com.zerobase.spendingalertservice.domain.User;
import com.zerobase.spendingalertservice.dto.LoginRequest;
import com.zerobase.spendingalertservice.dto.SignupRequest;
import com.zerobase.spendingalertservice.service.UserService;
import com.zerobase.spendingalertservice.util.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    List<String> roles = List.of("ROLE_" + user.getRole().name());

    String token = jwtUtil.generateToken(user.getEmail(), roles);

    return ResponseEntity.ok(token);
  }

}
