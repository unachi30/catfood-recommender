package com.catfood.demo.catcan.dto;

import com.catfood.demo.common.ApiResponse;
import com.catfood.demo.catcan.entity.CatCan;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "CatCanListApiResponse", description = "罐頭清單 API 回應")
public class CatCanListApiResponse extends ApiResponse<List<CatCan>> {

  @Override
  @Schema(description = "罐頭清單")
  public List<CatCan> getData() {
    return super.getData();
  }
}
