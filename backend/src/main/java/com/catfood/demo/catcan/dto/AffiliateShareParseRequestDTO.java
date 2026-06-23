package com.catfood.demo.catcan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "AffiliateShareParseRequest", description = "解析商城分享文案或分潤連結")
public class AffiliateShareParseRequestDTO {

  @Schema(
      description = "蝦皮/momo 等分享全文，或僅貼分潤連結",
      example = "『商品標題』，售價$928！分享給你 https://s.shopee.tw/xxx")
  @JsonProperty("shareText")
  @NotBlank(message = "請貼上分享文案或分潤連結")
  @Size(max = 4000, message = "內容過長")
  private String shareText;

  public String getShareText() {
    return shareText;
  }

  public void setShareText(String shareText) {
    this.shareText = shareText;
  }
}
