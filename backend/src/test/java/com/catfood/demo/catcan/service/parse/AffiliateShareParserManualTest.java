package com.catfood.demo.catcan.service.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.catfood.demo.catcan.dto.CatCanDraftDTO;
import java.util.List;
import org.junit.jupiter.api.Test;

class AffiliateShareParserManualTest {

  private final AffiliateShareParser parser =
      new AffiliateShareParser(
          List.of(
              new ShopeeShareParser(),
              new MomoShareParser(),
              new CoupangShareParser(new CoupangLinkResolver()),
              new UnknownShareParser()));

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
