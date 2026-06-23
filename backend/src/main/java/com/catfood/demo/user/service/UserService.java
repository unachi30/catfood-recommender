package com.catfood.demo.user.service;

import com.catfood.demo.user.dto.UserRegistrationRequestDTO;
import com.catfood.demo.user.entity.User;
import com.catfood.demo.user.entity.UserRole;
import com.catfood.demo.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public String register(UserRegistrationRequestDTO request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "帳號已存在");
    }
    if (userRepository.existsByIdentityCard(request.getIdentityCard())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "身分證字號已註冊");
    }

    User user = new User();
    user.setUsername(request.getUsername().trim());
    user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
    user.setIdentityCard(request.getIdentityCard().trim().toUpperCase());
    user.setRealName(trimToNull(request.getRealName()));
    user.setPhone(trimToNull(request.getPhone()));
    user.setEmail(trimToNull(request.getEmail()));
    user.setRole(UserRole.USER);

    userRepository.save(user);
    return "註冊成功";
  }

  private static String trimToNull(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
