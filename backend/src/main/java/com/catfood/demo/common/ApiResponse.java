package com.catfood.demo.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiResponse", description = "統一 API 回應格式")
public class ApiResponse<T> {

  @Schema(description = "狀態碼", example = "200")
  @JsonProperty("code")
  private int code;

  @Schema(description = "提示訊息", example = "登入成功")
  @JsonProperty("message")
  private String message;

  @Schema(description = "回應資料")
  @JsonProperty("data")
  private T data;

  public ApiResponse() {}

  public ApiResponse(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(200, message, data);
  }

  public static <T> ApiResponse<T> error(int code, String message) {
    return new ApiResponse<>(code, message, null);
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public T getData() {
    return data;
  }
}
