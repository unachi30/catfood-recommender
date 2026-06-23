package com.catfood.demo.catcan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CatCanConflictResolution", description = "衝突口味的處理方式")
public class CatCanConflictResolutionDTO {

  @JsonProperty("flavor")
  @NotBlank
  private String flavor;

  @Schema(description = "true=使用新版覆蓋；false=保留資料庫現有")
  @JsonProperty("useNew")
  @NotNull
  private Boolean useNew;

  public String getFlavor() {
    return flavor;
  }

  public void setFlavor(String flavor) {
    this.flavor = flavor;
  }

  public Boolean getUseNew() {
    return useNew;
  }

  public void setUseNew(Boolean useNew) {
    this.useNew = useNew;
  }
}
