package com.catfood.demo.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "identity_card")
    })
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 64)
  private String username;

  @Column(nullable = false, length = 128)
  private String password;

  @Column(name = "identity_card", nullable = false, length = 10)
  private String identityCard;

  @Column(name = "real_name", length = 64)
  private String realName;

  @Column(length = 16)
  private String phone;

  @Column(length = 128)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private UserRole role = UserRole.USER;

  public String displayName() {
    return realName != null && !realName.isBlank() ? realName : username;
  }
}
