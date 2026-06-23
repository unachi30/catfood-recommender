package com.catfood.demo.auth;

import com.catfood.demo.user.entity.User;
import com.catfood.demo.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public LoginResponseDTO login(LoginRequestDTO request) {
    User user =
        userRepository
            .findByUsername(request.getUsername().trim())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "帳號或密碼錯誤"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "帳號或密碼錯誤");
    }

    String token = jwtService.generateToken(user);
    return new LoginResponseDTO(token, user.getUsername(), user.displayName(), user.getRole());
  }
}
