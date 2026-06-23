package com.catfood.demo.catcan.dto;

import com.catfood.demo.catcan.entity.CatCan;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CatCanFlavorConflict", description = "同商品同口味的資料衝突")
public class CatCanFlavorConflictDTO {

  @JsonProperty("flavor")
  private String flavor;

  @JsonProperty("existing")
  private CatCan existing;

  @JsonProperty("incoming")
  private CatCanIncomingDTO incoming;

  @JsonProperty("duplicateCount")
  private int duplicateCount;

  public String getFlavor() {
    return flavor;
  }

  public void setFlavor(String flavor) {
    this.flavor = flavor;
  }

  public CatCan getExisting() {
    return existing;
  }

  public void setExisting(CatCan existing) {
    this.existing = existing;
  }

  public CatCanIncomingDTO getIncoming() {
    return incoming;
  }

  public void setIncoming(CatCanIncomingDTO incoming) {
    this.incoming = incoming;
  }

  public int getDuplicateCount() {
    return duplicateCount;
  }

  public void setDuplicateCount(int duplicateCount) {
    this.duplicateCount = duplicateCount;
  }
}
