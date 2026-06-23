package com.catfood.demo.catcan.service.stock;

import com.catfood.demo.catcan.service.parse.SharePlatform;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProductLinkChecker {

  private static final String USER_AGENT =
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

  private final HttpClient httpClient;
  private final int connectTimeoutSeconds;
  private final int readTimeoutSeconds;

  public ProductLinkChecker(
      @Value("${app.stock-sync.connect-timeout-seconds:10}") int connectTimeoutSeconds,
      @Value("${app.stock-sync.read-timeout-seconds:15}") int readTimeoutSeconds) {
    this.connectTimeoutSeconds = connectTimeoutSeconds;
    this.readTimeoutSeconds = readTimeoutSeconds;
    this.httpClient =
        HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
            .build();
  }

  public ProductLinkCheckResult check(String shopUrl) {
    if (shopUrl == null || shopUrl.isBlank()) {
      return ProductLinkCheckResult.unavailable("缺少購買連結");
    }

    SharePlatform platform = SharePlatform.fromUrl(shopUrl);

    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(shopUrl.trim()))
              .timeout(Duration.ofSeconds(readTimeoutSeconds))
              .header("User-Agent", USER_AGENT)
              .header("Accept-Language", "zh-TW,zh;q=0.9,en;q=0.8")
              .GET()
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      int status = response.statusCode();
      if (ProductPageAnalyzer.shouldSkipStatus(status)) {
        return ProductLinkCheckResult.skipped("HTTP " + status + "，略過不更新");
      }

      if (status == 404 || status == 410) {
        return ProductLinkCheckResult.unavailable("HTTP " + status);
      }

      String body = response.body();
      if (ProductPageAnalyzer.isDefinitelyUnavailable(platform, status, body)) {
        return ProductLinkCheckResult.unavailable("頁面顯示商品不可用");
      }

      if (status >= 200 && status < 300) {
        return ProductLinkCheckResult.available("連結有效");
      }

      return ProductLinkCheckResult.unavailable("HTTP " + status);
    } catch (IOException | InterruptedException ex) {
      if (ex instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      return ProductLinkCheckResult.skipped("連線失敗：" + ex.getMessage());
    }
  }
}
