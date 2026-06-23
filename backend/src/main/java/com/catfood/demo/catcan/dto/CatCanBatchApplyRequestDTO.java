package com.catfood.demo.catcan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "CatCanBatchApplyRequest", description = "批次新增並處理口味衝突")
public class CatCanBatchApplyRequestDTO extends CatCanBulkCreateRequestDTO {

  @JsonProperty("resolutions")
  @Valid
  private List<CatCanConflictResolutionDTO> resolutions = new ArrayList<>();

  public List<CatCanConflictResolutionDTO> getResolutions() {
    return resolutions;
  }

  public void setResolutions(List<CatCanConflictResolutionDTO> resolutions) {
    this.resolutions = resolutions;
  }
}
