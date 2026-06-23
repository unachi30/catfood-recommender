package com.catfood.demo.catcan.service.stock;

public record ProductLinkCheckResult(boolean skipped, boolean inStock, String reason) {

  public static ProductLinkCheckResult skipped(String reason) {
    return new ProductLinkCheckResult(true, true, reason);
  }

  public static ProductLinkCheckResult available(String reason) {
    return new ProductLinkCheckResult(false, true, reason);
  }

  public static ProductLinkCheckResult unavailable(String reason) {
    return new ProductLinkCheckResult(false, false, reason);
  }
}
