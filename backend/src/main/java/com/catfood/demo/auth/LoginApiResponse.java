package com.catfood.demo.auth;

import com.catfood.demo.common.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginApiResponse", description = "登入 API 回應")
public class LoginApiResponse extends ApiResponse<LoginResponseDTO> {

  @Override
  @Schema(description = "登入成功後的使用者資料")
  public LoginResponseDTO getData() {
    return super.getData();
  }
}
