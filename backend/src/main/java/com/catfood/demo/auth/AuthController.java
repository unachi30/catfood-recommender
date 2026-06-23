package com.catfood.demo.auth;

import com.catfood.demo.common.ApiResponse;
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

@Tag(name = "Auth", description = "登入驗證")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @Operation(summary = "登入", description = "驗證帳密並回傳 JWT 與角色（USER / ADMIN）")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "登入成功",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "帳號或密碼錯誤")
  })
  @PostMapping("/login")
  public ApiResponse<LoginResponseDTO> login(
      @Valid @RequestBody @Schema(description = "登入帳密") LoginRequestDTO request) {
    LoginResponseDTO result = authService.login(request);
    return ApiResponse.success("登入成功", result);
  }
}
