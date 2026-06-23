package com.catfood.demo.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "UserRegistrationRequest", description = "用戶註冊請求")
public class UserRegistrationRequestDTO {

  @Schema(description = "使用者帳號", example = "member02")
  @JsonProperty("username")
  @NotBlank(message = "帳號為必填")
  @Size(max = 64, message = "帳號長度不可超過 64 字")
  private String username;

  @Schema(description = "使用者密碼", example = "member123")
  @JsonProperty("password")
  @NotBlank(message = "密碼為必填")
  @Size(min = 4, max = 128, message = "密碼長度須為 4～128 字")
  private String password;

  @Schema(description = "身分證字號", example = "A123456789")
  @JsonProperty("identityCard")
  @NotBlank(message = "身分證字號為必填")
  @Pattern(
      regexp = "^[A-Z][12]\\d{8}$",
      message = "身分證格式須為英文字母 + 1 或 2 + 8 位數字，例：A123456789")
  private String identityCard;

  @Schema(description = "真實姓名", example = "王小明")
  @JsonProperty("realName")
  @Size(max = 64, message = "姓名長度不可超過 64 字")
  private String realName;

  @Schema(description = "手機號碼", example = "0912345678")
  @JsonProperty("phone")
  @Pattern(regexp = "^$|^09\\d{8}$", message = "手機須為 09 開頭共 10 碼")
  private String phone;

  @Schema(description = "Email", example = "user@example.com")
  @JsonProperty("email")
  @Email(message = "Email 格式不正確")
  @Size(max = 128, message = "Email 長度不可超過 128 字")
  private String email;

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

  public String getIdentityCard() {
    return identityCard;
  }

  public void setIdentityCard(String identityCard) {
    this.identityCard = identityCard;
  }

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
