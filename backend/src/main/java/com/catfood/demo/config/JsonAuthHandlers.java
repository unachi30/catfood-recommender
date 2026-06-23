package com.catfood.demo.config;

import com.catfood.demo.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JsonAuthHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  public JsonAuthHandlers() {
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    write(response, HttpStatus.UNAUTHORIZED.value(), "請先登入");
  }

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    write(response, HttpStatus.FORBIDDEN.value(), "需要管理員權限");
  }

  private void write(HttpServletResponse response, int code, String message) throws IOException {
    response.setStatus(code);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), ApiResponse.error(code, message));
  }
}
