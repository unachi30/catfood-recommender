package com.catfood.demo.catcan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "CatCanBatchCheckResult", description = "批次新增前的衝突檢查結果")
public class CatCanBatchCheckResultDTO {

  @JsonProperty("conflicts")
  private List<CatCanFlavorConflictDTO> conflicts = new ArrayList<>();

  @JsonProperty("newFlavors")
  private List<String> newFlavors = new ArrayList<>();

  public List<CatCanFlavorConflictDTO> getConflicts() {
    return conflicts;
  }

  public void setConflicts(List<CatCanFlavorConflictDTO> conflicts) {
    this.conflicts = conflicts;
  }

  public List<String> getNewFlavors() {
    return newFlavors;
  }

  public void setNewFlavors(List<String> newFlavors) {
    this.newFlavors = newFlavors;
  }
}
