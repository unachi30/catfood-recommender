package com.catfood.demo.catcan.controller;

import com.catfood.demo.catcan.dto.CatCanListApiResponse;
import com.catfood.demo.catcan.entity.CatCan;
import com.catfood.demo.catcan.service.CatCanService;
import com.catfood.demo.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cat Cans", description = "貓罐頭清單")
@RestController
@RequestMapping("/api/cans")
public class CatCanController {

  private final CatCanService catCanService;

  public CatCanController(CatCanService catCanService) {
    this.catCanService = catCanService;
  }

  @Operation(summary = "取得罐頭清單", description = "回傳所有貓罐頭資料（訪客可瀏覽）")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "查詢成功",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CatCanListApiResponse.class)))
  })
  @GetMapping
  public ApiResponse<List<CatCan>> getAll() {
    return ApiResponse.success("查詢成功", catCanService.findAll());
  }
}
