package com.catfood.demo.user.repository;

import com.catfood.demo.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByUsername(String username);

  boolean existsByIdentityCard(String identityCard);

  Optional<User> findByUsername(String username);
}
