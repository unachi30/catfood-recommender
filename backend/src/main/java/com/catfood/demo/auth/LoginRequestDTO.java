package com.catfood.demo.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "LoginRequest", description = "登入請求")
public class LoginRequestDTO {

  @Schema(description = "使用者帳號", example = "admin")
  @JsonProperty("username")
  @NotBlank(message = "帳號為必填")
  @Size(max = 64, message = "帳號長度不可超過 64 字")
  private String username;

  @Schema(description = "使用者密碼", example = "admin123")
  @JsonProperty("password")
  @NotBlank(message = "密碼為必填")
  @Size(max = 128, message = "密碼長度不可超過 128 字")
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
