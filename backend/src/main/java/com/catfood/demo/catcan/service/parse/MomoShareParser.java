package com.catfood.demo.catcan.service.parse;

import com.catfood.demo.catcan.dto.CatCanDraftDTO;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
class MomoShareParser implements PlatformShareParser {

  private static final Pattern PRICE_PATTERN =
      Pattern.compile("【\\s*\\$?([\\d,]+(?:\\.\\d+)?)\\s*元\\s*】");

  private static final Pattern[] PACK_PATTERNS = {
    Pattern.compile("(\\d+)g\\s*[xX×]\\s*(\\d+)\\s*入", Pattern.CASE_INSENSITIVE),
    Pattern.compile("(\\d+)\\s*g\\s*[xX×]\\s*(\\d+)\\s*入", Pattern.CASE_INSENSITIVE),
    Pattern.compile("[xX×]\\s*(\\d+)\\s*入", Pattern.CASE_INSENSITIVE),
    Pattern.compile("(\\d+)\\s*入\\s*/", Pattern.CASE_INSENSITIVE),
  };

  @Override
  public SharePlatform platform() {
    return SharePlatform.MOMO;
  }

  @Override
  public void parse(String rawInput, String url, String textWithoutUrl, CatCanDraftDTO draft) {
    draft.setShopName("momo");
    draft.setShopUrl(ShareParseSupport.cleanupUrl(url));

    applyPrice(textWithoutUrl, draft);
    applyPackCount(textWithoutUrl, draft);
    ShareParseSupport.applyCapacity(draft, textWithoutUrl);
    draft.setCategory(ShareParseSupport.detectCategory(textWithoutUrl));
    ShareParseSupport.applyBrandAndName(draft, normalizeMomoLine(textWithoutUrl), textWithoutUrl);
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
    for (int i = 0; i < PACK_PATTERNS.length; i++) {
      Matcher matcher = PACK_PATTERNS[i].matcher(text);
      if (matcher.find()) {
        int count = i == 0 || i == 1 ? Integer.parseInt(matcher.group(2)) : Integer.parseInt(matcher.group(1));
        draft.setTotalCount(count);
        return;
      }
    }
  }

  private static String normalizeMomoLine(String text) {
    String line = text;
    line = line.replaceAll("【\\s*\\$?[\\d,]+(?:\\.\\d+)?\\s*元\\s*】", " ");
    line = line.replaceAll("(\\d+)g\\s*[xX×]\\s*(\\d+)\\s*入/?[^\\s]*", " ");
    line = line.replaceAll("\\d+\\s*g\\s*[xX×]\\s*\\d+\\s*入/?[^\\s]*", " ");
    line = line.replaceAll("\\d+\\s*g", " ");
    line = line.replaceAll("\\([^)]*\\)", " ");
    line = line.replaceAll("/\\s*箱", " ");
    line = line.replaceAll("[，。！!？?]+$", "");
    line = line.replaceAll("\\s+", " ").trim();
    return line;
  }
}
