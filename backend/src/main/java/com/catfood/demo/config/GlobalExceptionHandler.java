package com.catfood.demo.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.catfood.demo.common.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
    FieldError fieldError = ex.getBindingResult().getFieldError();
    String message = fieldError != null ? fieldError.getDefaultMessage() : "欄位驗證失敗";
    return ResponseEntity.badRequest().body(ApiResponse.error(400, message));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiResponse<Void>> handleStatus(ResponseStatusException ex) {
    int code = ex.getStatusCode().value();
    String message = ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString();
    return ResponseEntity.status(ex.getStatusCode()).body(ApiResponse.error(code, message));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(409, "商品資料重複，請檢查是否已新增過（或刪除舊資料後重試）"));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error(403, "需要管理員權限"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(500, "伺服器發生錯誤，請稍後再試"));
  }
}
