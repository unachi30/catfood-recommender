package com.catfood.demo.catcan.service.parse;

import com.catfood.demo.catcan.dto.CatCanDraftDTO;
import org.springframework.stereotype.Component;

@Component
class CoupangShareParser implements PlatformShareParser {

  private final CoupangLinkResolver linkResolver;

  CoupangShareParser(CoupangLinkResolver linkResolver) {
    this.linkResolver = linkResolver;
  }

  @Override
  public SharePlatform platform() {
    return SharePlatform.COUPANG;
  }

  @Override
  public void parse(String rawInput, String url, String textWithoutUrl, CatCanDraftDTO draft) {
    draft.setShopName("酷澎");
    draft.setShopUrl(ShareParseSupport.cleanupUrl(url));
    draft.setCategory("主食罐");

    boolean linkOnly = ShareParseSupport.isLinkOnlyInput(rawInput);
    CoupangLinkResolver.CoupangLinkInfo linkInfo = linkResolver.resolve(url);

    if (linkOnly) {
      draft.addHint("酷澎分享通常只有連結，商品名稱與價格需至商品頁查看後手動填寫");
    }

    if (linkInfo != null && linkInfo.hasPageKey()) {
      String productPageUrl = linkInfo.productPageUrl();
      if (productPageUrl != null) {
        draft.setDescription("商品頁參考：" + productPageUrl);
      }
      draft.addHint("已從分潤連結解析商品 ID（pageKey=" + linkInfo.pageKey() + "），請開啟商品頁複製標題");
    } else if (linkOnly) {
      draft.addHint("無法從短連結解析商品 ID，請直接開啟連結後手動填寫各欄位");
    }

    if (!textWithoutUrl.isBlank()) {
      ShareParseSupport.applyCapacity(draft, textWithoutUrl);
      ShareParseSupport.applyBrandAndName(draft, textWithoutUrl, textWithoutUrl);
      draft.addHint("已從附加文字推測部分欄位，請確認");
    } else {
      draft.addHint("無法自動解析價格，請手動填寫");
      draft.addHint("無法自動解析容量，請手動填寫（例如 80g）");
    }

    draft.addHint("酷澎商品頁有防爬蟲限制，後端無法自動掃描完整商品資訊");
    draft.addHint("評分請至商城商品頁查看後手動填入");
  }
}
