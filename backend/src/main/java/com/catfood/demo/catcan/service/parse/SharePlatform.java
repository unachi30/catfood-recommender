package com.catfood.demo.catcan.service.parse;

public enum SharePlatform {
  SHOPEE,
  MOMO,
  COUPANG,
  UNKNOWN;

  public static SharePlatform fromUrl(String url) {
    if (url == null || url.isBlank()) {
      return UNKNOWN;
    }
    String lower = url.toLowerCase();
    if (lower.contains("shopee")) {
      return SHOPEE;
    }
    if (lower.contains("momo")) {
      return MOMO;
    }
    if (lower.contains("coupa") || lower.contains("coupang")) {
      return COUPANG;
    }
    return UNKNOWN;
  }
}
