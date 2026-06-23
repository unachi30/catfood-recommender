package com.catfood.demo.catcan.service.parse;

import com.catfood.demo.catcan.dto.CatCanDraftDTO;
import com.catfood.demo.catcan.service.CatCanTextNormalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ShareParseSupport {

  /** Allow ? and & so query params (pageKey, share_channel_code) stay in the URL. */
  static final Pattern URL_PATTERN =
      Pattern.compile("(https?://[^\\s\\u3000，。！!\"'<>（）]+)", Pattern.CASE_INSENSITIVE);

  static final Pattern CAPACITY_PATTERN = Pattern.compile("(\\d+)\\s*g", Pattern.CASE_INSENSITIVE);

  /** e.g. Cherie 法麗, WELLNESS CORE */
  static final Pattern LATIN_BRAND_PATTERN =
      Pattern.compile("(?i)([A-Za-z][A-Za-z.'\\-&]*(?:\\s+[\\u4e00-\\u9fff]{1,6})?)");

  /** e.g. 全照護主食罐（排除 貓主食罐、貓罐頭 等商城標籤） */
  static final Pattern PRODUCT_CAN_NAME_PATTERN =
      Pattern.compile("([\\u4e00-\\u9fff]+(?:副食|主食)罐)");

  private ShareParseSupport() {}

  static String extractUrl(String input) {
    Matcher matcher = URL_PATTERN.matcher(input);
    if (matcher.find()) {
      return matcher.group(1);
    }
    if (input.startsWith("http://") || input.startsWith("https://")) {
      return input.split("\\s")[0];
    }
    return null;
  }

  static String cleanupUrl(String url) {
    return url.replaceAll("[，。！!？?]+$", "");
  }

  static String textWithoutUrls(String input) {
    return input.replaceAll("https?://\\S+", " ").trim();
  }

  static boolean isLinkOnlyInput(String input) {
    String trimmed = input == null ? "" : input.trim();
    if (trimmed.isBlank()) {
      return false;
    }
    String url = extractUrl(trimmed);
    if (url == null) {
      return false;
    }
    String withoutUrl = trimmed.replace(url, "").trim();
    return withoutUrl.isBlank();
  }

  static String detectCategory(String input) {
    if (input.contains("副食")) {
      return "副食罐";
    }
    if (input.contains("主食")) {
      return "主食罐";
    }
    return "主食罐";
  }

  static void applyCapacity(CatCanDraftDTO draft, String text) {
    Matcher capacityMatcher = CAPACITY_PATTERN.matcher(text);
    if (capacityMatcher.find()) {
      draft.setCapacity(capacityMatcher.group(1) + "g");
    } else {
      draft.addHint("無法自動解析容量，請手動填寫（例如 80g）");
    }
  }

  static void applyUnitPrice(CatCanDraftDTO draft) {
    if (draft.getPrice() != null && draft.getTotalCount() != null && draft.getTotalCount() > 0) {
      draft.setUnitPrice(round2(draft.getPrice() / draft.getTotalCount()));
    }
  }

  static void applyDefaults(CatCanDraftDTO draft) {
    if (draft.getTotalCount() == null || draft.getTotalCount() < 1) {
      draft.setTotalCount(1);
    }
    if (draft.getUnitPrice() == null && draft.getPrice() != null && draft.getPrice() > 0) {
      draft.setUnitPrice(round2(draft.getPrice() / draft.getTotalCount()));
    }
  }

  static void applyBrandAndName(CatCanDraftDTO draft, String normalizedLine, String rawTextForParen) {
    String line = normalizedLine;
    String brand = extractLeadingBracketBrand(line);
    if (brand != null) {
      line = line.substring(line.indexOf('】') + 1).trim();
    } else {
      brand = extractLatinBrand(line);
      if (brand != null) {
        line = line.replaceFirst(Pattern.quote(brand), "").trim();
      }
    }

    String name = extractProductCanName(line);
    if (name == null) {
      name = extractProductCanNameFallback(line);
    }

    draft.setBrand(
        brand != null ? CatCanTextNormalizer.normalizeBrandOrName(brand) : "");
    String rawName = name != null && !name.isBlank() ? name : line;
    draft.setName(CatCanTextNormalizer.normalizeBrandOrName(rawName));

    String parenDetail = extractParenDetail(rawTextForParen);
    StringBuilder desc = new StringBuilder();
    if (parenDetail != null && !parenDetail.isBlank()) {
      desc.append(parenDetail);
    }
    if (draft.getTotalCount() != null && draft.getTotalCount() > 1) {
      if (desc.length() > 0) {
        desc.append("。 ");
      }
      desc.append(draft.getTotalCount()).append(" 入組合");
    }
    if (desc.length() > 0) {
      draft.setDescription(desc.toString());
    }
  }

  static String extractLeadingBracketBrand(String line) {
    Matcher brandMatcher = Pattern.compile("^【([^】]+)】").matcher(line);
    if (brandMatcher.find()) {
      String brand = brandMatcher.group(1).trim();
      if (!brand.matches("\\d+罐組")) {
        return brand;
      }
    }
    return null;
  }

  static String extractLatinBrand(String line) {
    Matcher matcher = LATIN_BRAND_PATTERN.matcher(line);
    if (matcher.find()) {
      return matcher.group(1).trim();
    }
    return null;
  }

  static String extractProductCanName(String line) {
    Matcher matcher = PRODUCT_CAN_NAME_PATTERN.matcher(line);
    while (matcher.find()) {
      String candidate = matcher.group(1).trim();
      if (!candidate.startsWith("貓")) {
        return candidate;
      }
    }
    return null;
  }

  static String extractProductCanNameFallback(String line) {
    if (line.isBlank()) {
      return null;
    }
    String cleaned =
        line.replaceAll("(貓食|全齡貓|幼貓|成貓|老貓|慕斯口感|貓主食罐|貓罐頭|貓罐|貓副食罐)", " ")
            .replaceAll("\\s+", " ")
            .trim();
    return cleaned.isBlank() ? null : cleaned;
  }

  static String extractParenDetail(String text) {
    Matcher parenMatcher = Pattern.compile("\\(([^)]+)\\)").matcher(text);
    return parenMatcher.find() ? parenMatcher.group(1).trim() : null;
  }

  static double parseNumber(String raw) {
    return Double.parseDouble(raw.replace(",", ""));
  }

  static double round2(double value) {
    return Math.round(value * 100.0) / 100.0;
  }

  static void addCommonHints(CatCanDraftDTO draft) {
    draft.addHint("品牌與名稱為自動推測，儲存前請確認");
    draft.addHint("評分請至商城商品頁查看後手動填入");
  }
}
