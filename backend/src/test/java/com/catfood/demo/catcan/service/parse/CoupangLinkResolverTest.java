package com.catfood.demo.catcan.service.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CoupangLinkResolverTest {

  private final CoupangLinkResolver resolver = new CoupangLinkResolver();

  @Test
  void parsePageKeyFromFullCoupangShareUrl() {
    String url =
        "https://link.tw.coupang.com/gl/tw/CSHARESDP?lptag=CFM54100573&pageKey=678148353851451&itemId=263554422751260&vendorItemId=263556347953160";

    CoupangLinkResolver.CoupangLinkInfo info = resolver.resolve(url);

    assertNotNull(info);
    assertEquals("678148353851451", info.pageKey());
    assertEquals("263554422751260", info.itemId());
    assertEquals(
        "https://www.tw.coupang.com/products/678148353851451?itemId=263554422751260&vendorItemId=263556347953160",
        info.productPageUrl());
  }

  @Test
  void parsePageKeyFromLinkTwShareUrlWithoutLptag() {
    String url =
        "https://link.tw.coupang.com/gl/tw/CSHARESDP?pageKey=678148353851451&itemId=263554422751260&vendorItemId=263556347953160";

    CoupangLinkResolver.CoupangLinkInfo info = resolver.resolve(url);

    assertEquals("678148353851451", info.pageKey());
  }

  @Test
  void extractUrlKeepsQueryParameters() {
    String input =
        "https://link.tw.coupang.com/gl/tw/CSHARESDP?pageKey=678148353851451&itemId=263554422751260";

    String url = ShareParseSupport.extractUrl(input);

    assertEquals(input, url);
    assertEquals("678148353851451", resolver.resolve(url).pageKey());
  }

  @Test
  void parsePageKeyFromNestedAfRParameter() {
    String url =
        "https://coupang.onelink.me/yowQ?af_r=https%3A%2F%2Flink.tw.coupang.com%2Fgl%2Ftw%2FCSHARESDP%3FpageKey%3D678148353851451%26itemId%3D263554422751260";

    CoupangLinkResolver.CoupangLinkInfo info = resolver.resolve(url);

    assertNotNull(info);
    assertEquals("678148353851451", info.pageKey());
    assertEquals("263554422751260", info.itemId());
  }
}
