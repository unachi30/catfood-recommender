package com.catfood.demo.auth;

import com.catfood.demo.user.entity.User;
import com.catfood.demo.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey secretKey;
  private final long expirationMs;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  public String generateToken(User user) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
        .subject(user.getUsername())
        .claim("userId", user.getId())
        .claim("role", user.getRole().name())
        .claim("name", user.displayName())
        .issuedAt(now)
        .expiration(expiry)
        .signWith(secretKey)
        .compact();
  }

  public Claims parseClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  public UserRole extractRole(Claims claims) {
    return UserRole.valueOf(claims.get("role", String.class));
  }
}
