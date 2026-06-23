package com.catfood.demo.catcan.service.parse;

import com.catfood.demo.catcan.dto.CatCanDraftDTO;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
class ShopeeShareParser implements PlatformShareParser {

  private static final Pattern PRICE_PATTERN =
      Pattern.compile("售價\\s*\\$?([\\d,]+(?:\\.\\d+)?)", Pattern.CASE_INSENSITIVE);

  private static final Pattern PACK_PATTERN = Pattern.compile("【(\\d+)罐組】");

  @Override
  public SharePlatform platform() {
    return SharePlatform.SHOPEE;
  }

  @Override
  public void parse(String rawInput, String url, String textWithoutUrl, CatCanDraftDTO draft) {
    draft.setShopName("Shopee");
    draft.setShopUrl(ShareParseSupport.cleanupUrl(url));

    applyPrice(textWithoutUrl, draft);
    applyPackCount(textWithoutUrl, draft);
    ShareParseSupport.applyCapacity(draft, textWithoutUrl);
    draft.setCategory(ShareParseSupport.detectCategory(textWithoutUrl));
    ShareParseSupport.applyBrandAndName(draft, normalizeShopeeLine(textWithoutUrl), textWithoutUrl);
    ShareParseSupport.applyUnitPrice(draft);
    ShareParseSupport.addCommonHints(draft);
  }

  private static void applyPrice(String text, CatCanDraftDTO draft) {
    Matcher matcher = PRICE_PATTERN.matcher(text);
    if (matcher.find()) {
      draft.setPrice(ShareParseSupport.parseNumber(matcher.group(1)));
    } else {
      draft.addHint("無法自動解析價格，請手動填寫");
    }
  }

  private static void applyPackCount(String text, CatCanDraftDTO draft) {
    Matcher matcher = PACK_PATTERN.matcher(text);
    if (matcher.find()) {
      draft.setTotalCount(Integer.parseInt(matcher.group(1)));
    }
  }

  private static String normalizeShopeeLine(String text) {
    String line = text;
    line = line.replaceAll("分享給你.*", "");
    line = line.replaceAll("^我發現超棒的東西\\s*", "");
    line = line.replaceAll("『『[^』]*』", "");
    line = line.replaceAll("[『』]", "");
    line = line.replaceAll("售價\\s*\\$?[\\d,]+(?:\\.\\d+)?[^\\s]*", " ");
    line = line.replaceAll("【\\d+罐組】", " ");
    line = line.replaceAll("\\d+\\s*g", " ");
    line = line.replaceAll("\\([^)]*\\)", " ");
    line = line.replaceAll("[，。！!？?]+$", "");
    line = line.replaceAll("\\s+", " ").trim();
    return line;
  }
}
