package com.catfood.demo.auth;

import com.catfood.demo.user.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponse", description = "登入成功後的使用者資料")
public class LoginResponseDTO {

  @Schema(description = "JWT 存取權杖", example = "eyJhbGciOiJIUzI1NiJ9...")
  @JsonProperty("token")
  private String token;

  @Schema(description = "使用者帳號", example = "admin")
  @JsonProperty("username")
  private String username;

  @Schema(description = "使用者顯示名稱", example = "系統管理員")
  @JsonProperty("displayName")
  private String name;

  @Schema(description = "使用者角色", example = "ADMIN")
  @JsonProperty("role")
  private UserRole role;

  public LoginResponseDTO(String token, String username, String name, UserRole role) {
    this.token = token;
    this.username = username;
    this.name = name;
    this.role = role;
  }

  public String getToken() {
    return token;
  }

  public String getUsername() {
    return username;
  }

  public String getName() {
    return name;
  }

  public UserRole getRole() {
    return role;
  }
}
