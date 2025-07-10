package com.zerobase.spendingalertservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secretKeyString;

  private SecretKey secretKey;

  private final long expirationTime = 1000 * 60 * 60; // 1시간

  @PostConstruct
  public void init() {
    this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
  }

  // 토큰 생성
  public String generateToken(String email, String role) {
    Claims claims = Jwts.claims().setSubject(email);
    claims.put("role", role);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  // Claims 가져오기
  public Claims getClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  // 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      // 서명 오류, 만료, 잘못된 형식 등등
      return false;
    }
  }
}
