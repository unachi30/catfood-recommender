package com.catfood.demo.user.dto;

import com.catfood.demo.common.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RegisterApiResponse", description = "註冊 API 回應")
public class RegisterApiResponse extends ApiResponse<String> {

  @Override
  @Schema(description = "註冊結果說明", example = "註冊成功")
  public String getData() {
    return super.getData();
  }
}
