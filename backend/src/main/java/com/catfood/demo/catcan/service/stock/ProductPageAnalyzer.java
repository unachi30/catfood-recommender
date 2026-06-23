package com.catfood.demo.catcan.service.stock;

import com.catfood.demo.catcan.service.parse.SharePlatform;
import java.util.List;

final class ProductPageAnalyzer {

  private static final List<String> SHOPEE_UNAVAILABLE =
      List.of(
          "商品不存在",
          "此商品目前無法購買",
          "商品已下架",
          "此賣場已結束",
          "item is not available",
          "item not found",
          "product not found",
          "sorry, this item");

  private static final List<String> MOMO_UNAVAILABLE =
      List.of(
          "查無商品",
          "商品已下架",
          "售完補貨中",
          "目前無法購買",
          "很抱歉，找不到",
          "商品不存在",
          "此商品已下架");

  private static final List<String> COUPANG_UNAVAILABLE =
      List.of(
          "품절",
          "sold out",
          "目前無法購買",
          "商品不存在",
          "sorry, this item",
          "item is not available",
          "product not found");

  private static final List<String> GENERIC_UNAVAILABLE =
      List.of("商品不存在", "商品已下架", "目前無法購買", "not found", "not available");

  private ProductPageAnalyzer() {}

  static boolean isDefinitelyUnavailable(SharePlatform platform, int statusCode, String html) {
    if (statusCode == 404 || statusCode == 410) {
      return true;
    }
    if (statusCode < 200 || statusCode >= 300) {
      return false;
    }
    if (html == null || html.isBlank()) {
      return true;
    }
    String text = html.toLowerCase();
    return switch (platform) {
      case SHOPEE -> containsAny(text, SHOPEE_UNAVAILABLE);
      case MOMO -> containsAny(text, MOMO_UNAVAILABLE);
      case COUPANG -> containsAny(text, COUPANG_UNAVAILABLE);
      case UNKNOWN -> containsAny(text, GENERIC_UNAVAILABLE);
    };
  }

  static boolean shouldSkipStatus(int statusCode) {
    return statusCode == 403 || statusCode == 401 || statusCode == 429 || statusCode >= 500;
  }

  private static boolean containsAny(String text, List<String> phrases) {
    for (String phrase : phrases) {
      if (text.contains(phrase.toLowerCase())) {
        return true;
      }
    }
    return false;
  }
}
