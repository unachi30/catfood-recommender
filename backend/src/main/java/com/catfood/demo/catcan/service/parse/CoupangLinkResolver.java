package com.catfood.demo.catcan.service.parse;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
class CoupangLinkResolver {

  private static final Pattern PAGE_KEY_PATTERN =
      Pattern.compile("[?&]pageKey=(\\d+)", Pattern.CASE_INSENSITIVE);
  private static final Pattern ITEM_ID_PATTERN =
      Pattern.compile("[?&]itemId=(\\d+)", Pattern.CASE_INSENSITIVE);
  private static final Pattern VENDOR_ITEM_ID_PATTERN =
      Pattern.compile("[?&]vendorItemId=(\\d+)", Pattern.CASE_INSENSITIVE);

  CoupangLinkInfo resolve(String url) {
    if (url == null || url.isBlank()) {
      return null;
    }

    CoupangLinkInfo fromUrl = parseFromUrlString(url);
    if (fromUrl.hasPageKey()) {
      return fromUrl;
    }

    if (!url.toLowerCase().contains("coupa.ng")) {
      return fromUrl;
    }

    try {
      String redirectTarget = fetchFirstRedirectLocation(url);
      if (redirectTarget == null) {
        return fromUrl;
      }
      CoupangLinkInfo fromRedirect = parseFromUrlString(redirectTarget);
      return fromRedirect.hasPageKey() ? fromRedirect : fromUrl;
    } catch (Exception ex) {
      return fromUrl;
    }
  }

  private static CoupangLinkInfo parseFromUrlString(String url) {
    String decoded = decodeNestedUrls(url);
    String pageKey = firstGroup(PAGE_KEY_PATTERN, decoded);
    String itemId = firstGroup(ITEM_ID_PATTERN, decoded);
    String vendorItemId = firstGroup(VENDOR_ITEM_ID_PATTERN, decoded);
    return new CoupangLinkInfo(pageKey, itemId, vendorItemId);
  }

  private static String decodeNestedUrls(String url) {
    String current = url;
    for (int i = 0; i < 3; i++) {
      int afR = current.indexOf("af_r=");
      if (afR < 0) {
        break;
      }
      String encoded = current.substring(afR + 5);
      int amp = encoded.indexOf('&');
      if (amp >= 0) {
        encoded = encoded.substring(0, amp);
      }
      current = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }
    return current;
  }

  private static String firstGroup(Pattern pattern, String text) {
    Matcher matcher = pattern.matcher(text);
    return matcher.find() ? matcher.group(1) : null;
  }

  private static String fetchFirstRedirectLocation(String url) throws Exception {
    java.net.http.HttpClient client =
        java.net.http.HttpClient.newBuilder()
            .followRedirects(java.net.http.HttpClient.Redirect.NEVER)
            .connectTimeout(java.time.Duration.ofSeconds(8))
            .build();

    java.net.http.HttpRequest request =
        java.net.http.HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(java.time.Duration.ofSeconds(8))
            .header("User-Agent", "Mozilla/5.0 (compatible; CatfoodRecommender/1.0)")
            .GET()
            .build();

    java.net.http.HttpResponse<Void> response =
        client.send(request, java.net.http.HttpResponse.BodyHandlers.discarding());

    int status = response.statusCode();
    if (status >= 300 && status < 400) {
      return response.headers().firstValue("Location").orElse(null);
    }
    return null;
  }

  record CoupangLinkInfo(String pageKey, String itemId, String vendorItemId) {

    boolean hasPageKey() {
      return pageKey != null && !pageKey.isBlank();
    }

    String productPageUrl() {
      if (!hasPageKey()) {
        return null;
      }
      StringBuilder url = new StringBuilder("https://www.tw.coupang.com/products/").append(pageKey);
      if (itemId != null && !itemId.isBlank()) {
        url.append("?itemId=").append(itemId);
        if (vendorItemId != null && !vendorItemId.isBlank()) {
          url.append("&vendorItemId=").append(vendorItemId);
        }
      }
      return url.toString();
    }
  }
}
