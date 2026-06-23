package com.catfood.demo.config;

import com.catfood.demo.user.entity.User;
import com.catfood.demo.user.entity.UserRole;
import com.catfood.demo.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DevDataInitializer {

  @Bean
  CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return args -> {
      seedUser(
          userRepository,
          passwordEncoder,
          "admin",
          "admin123",
          "A123456789",
          "系統管理員",
          UserRole.ADMIN);
      seedUser(
          userRepository,
          passwordEncoder,
          "member01",
          "member123",
          "B223456789",
          "一般會員",
          UserRole.USER);
    };
  }

  private static void seedUser(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      String username,
      String rawPassword,
      String identityCard,
      String realName,
      UserRole role) {
    if (userRepository.existsByUsername(username)) {
      return;
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setIdentityCard(identityCard);
    user.setRealName(realName);
    user.setRole(role);
    userRepository.save(user);
  }
}
