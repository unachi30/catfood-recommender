package com.catfood.demo.catcan.service.stock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.catfood.demo.catcan.service.parse.SharePlatform;
import org.junit.jupiter.api.Test;

class ProductPageAnalyzerTest {

  @Test
  void detectsShopeeUnavailablePage() {
    String html = "<html><body>很抱歉，商品不存在</body></html>";
    assertTrue(ProductPageAnalyzer.isDefinitelyUnavailable(SharePlatform.SHOPEE, 200, html));
  }

  @Test
  void detectsMomoUnavailablePage() {
    String html = "<html><body>查無商品資料</body></html>";
    assertTrue(ProductPageAnalyzer.isDefinitelyUnavailable(SharePlatform.MOMO, 200, html));
  }

  @Test
  void treatsHealthyPageAsAvailable() {
    String html = "<html><body>加入購物車</body></html>";
    assertFalse(ProductPageAnalyzer.isDefinitelyUnavailable(SharePlatform.SHOPEE, 200, html));
  }

  @Test
  void skipsBlockedStatusCodes() {
    assertTrue(ProductPageAnalyzer.shouldSkipStatus(403));
    assertTrue(ProductPageAnalyzer.shouldSkipStatus(500));
    assertFalse(ProductPageAnalyzer.shouldSkipStatus(404));
  }
}
