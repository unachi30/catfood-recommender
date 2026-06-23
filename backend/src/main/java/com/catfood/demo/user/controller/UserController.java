package com.catfood.demo.user.controller;

import com.catfood.demo.common.ApiResponse;
import com.catfood.demo.user.dto.RegisterApiResponse;
import com.catfood.demo.user.dto.UserRegistrationRequestDTO;
import com.catfood.demo.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "用戶註冊")
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "用戶註冊", description = "建立新用戶（角色固定為 USER）")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "註冊成功",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "帳號或身分證已存在")
  })
  @PostMapping("/register")
  public ApiResponse<String> register(
      @Valid @RequestBody @Schema(description = "註冊資料") UserRegistrationRequestDTO request) {
    String result = userService.register(request);
    return ApiResponse.success("註冊成功", result);
  }
}
