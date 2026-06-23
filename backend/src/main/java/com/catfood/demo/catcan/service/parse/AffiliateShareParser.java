package com.catfood.demo.catcan.service.parse;

import com.catfood.demo.catcan.dto.CatCanDraftDTO;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AffiliateShareParser {

  private final Map<SharePlatform, PlatformShareParser> parsers;

  public AffiliateShareParser(List<PlatformShareParser> parserList) {
    this.parsers = new EnumMap<>(SharePlatform.class);
    for (PlatformShareParser parser : parserList) {
      this.parsers.put(parser.platform(), parser);
    }
  }

  public CatCanDraftDTO parse(String rawInput) {
    String input = rawInput == null ? "" : rawInput.trim();
    CatCanDraftDTO draft = new CatCanDraftDTO();
    draft.setInStock(true);

    String url = ShareParseSupport.extractUrl(input);
    if (url == null) {
      draft.addHint("找不到連結，請確認已貼上完整分潤網址");
      parsers.get(SharePlatform.UNKNOWN).parse(input, null, input, draft);
      ShareParseSupport.applyDefaults(draft);
      return draft;
    }

    String textWithoutUrl = ShareParseSupport.textWithoutUrls(input);
    SharePlatform platform = SharePlatform.fromUrl(url);
    PlatformShareParser parser = parsers.getOrDefault(platform, parsers.get(SharePlatform.UNKNOWN));
    parser.parse(input, url, textWithoutUrl, draft);
    ShareParseSupport.applyDefaults(draft);

    return draft;
  }
}
