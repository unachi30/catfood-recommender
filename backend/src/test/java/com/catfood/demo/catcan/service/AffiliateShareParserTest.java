package com.catfood.demo.catcan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.catfood.demo.catcan.dto.CatCanDraftDTO;
import com.catfood.demo.catcan.service.parse.AffiliateShareParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AffiliateShareParserTest {

  @Autowired private AffiliateShareParser parser;

  @Test
  void parseMomoShareText() {
    String input =
        """
        【Cherie 法麗】全照護主食罐 80gx24入/箱(貓主食罐 貓罐頭 貓食 全齡貓 幼貓 慕斯口感)【899元】
        https://s.momoshop.com.tw/s/07nsV7zw
        """;

    CatCanDraftDTO draft = parser.parse(input);

    assertEquals(899.0, draft.getPrice());
    assertEquals("momo", draft.getShopName());
    assertEquals("80g", draft.getCapacity());
    assertEquals(24, draft.getTotalCount());
    assertEquals("Cherie法麗", draft.getBrand());
    assertEquals("全照護主食罐", draft.getName());
    assertEquals("主食罐", draft.getCategory());
    assertNotNull(draft.getShopUrl());
  }

  @Test
  void parseShopeeShareText() {
    String input =
        """
        我發現超棒的東西『『㊆㊆犬貓館』【24罐組】Cherie 法麗 全照護主食罐  80g 貓食 全齡貓 幼貓 慕斯口感 貓主食罐 貓罐頭』，售價$928！分享給你 https://s.shopee.tw/20tGQNGq4A?share_channel_code=6
        """;

    CatCanDraftDTO draft = parser.parse(input);

    assertEquals(928.0, draft.getPrice());
    assertEquals("Shopee", draft.getShopName());
    assertEquals(24, draft.getTotalCount());
    assertEquals("80g", draft.getCapacity());
    assertEquals("Cherie法麗", draft.getBrand());
    assertEquals("全照護主食罐", draft.getName());
    assertEquals("主食罐", draft.getCategory());
    assertNotNull(draft.getShopUrl());
  }

  @Test
  void parseCoupangLinkOnly() {
    String input = "https://coupa.ng/cnyvsk";

    CatCanDraftDTO draft = parser.parse(input);

    assertEquals("酷澎", draft.getShopName());
    assertEquals("https://coupa.ng/cnyvsk", draft.getShopUrl());
    assertTrue(
        draft.getParseHints().stream().anyMatch(h -> h.contains("酷澎") || h.contains("手動")),
        () -> "expected coupang manual-fill hints but got: " + draft.getParseHints());
  }

  @Test
  void parseCoupangFullShareUrlExtractsProductReference() {
    String input =
        "https://link.tw.coupang.com/gl/tw/CSHARESDP?pageKey=678148353851451&itemId=263554422751260&vendorItemId=263556347953160";

    CatCanDraftDTO draft = parser.parse(input);

    assertEquals("酷澎", draft.getShopName());
    assertNotNull(draft.getDescription());
    assertTrue(draft.getDescription().contains("678148353851451"));
    assertTrue(draft.getParseHints().stream().anyMatch(h -> h.contains("pageKey")));
  }
}
