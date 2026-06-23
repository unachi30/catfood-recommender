package com.catfood.demo.catcan.service.parse;

import com.catfood.demo.catcan.dto.CatCanDraftDTO;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
class UnknownShareParser implements PlatformShareParser {

  @Override
  public SharePlatform platform() {
    return SharePlatform.UNKNOWN;
  }

  @Override
  public void parse(String rawInput, String url, String textWithoutUrl, CatCanDraftDTO draft) {
    if (url != null) {
      draft.setShopUrl(ShareParseSupport.cleanupUrl(url));
      draft.setShopName("其他");
    }
    draft.setCategory(ShareParseSupport.detectCategory(textWithoutUrl));
    ShareParseSupport.applyCapacity(draft, textWithoutUrl);
    ShareParseSupport.applyBrandAndName(draft, textWithoutUrl, textWithoutUrl);
    draft.addHint("無法辨識商城，已使用通用解析邏輯");
    ShareParseSupport.addCommonHints(draft);
  }
}
