package com.catfood.demo.catcan.service.stock;

import com.catfood.demo.catcan.entity.CatCan;
import com.catfood.demo.catcan.repository.CatCanRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatCanStockSyncService {

  private static final Logger log = LoggerFactory.getLogger(CatCanStockSyncService.class);

  private final CatCanRepository repository;
  private final ProductLinkChecker linkChecker;
  private final long requestDelayMs;

  public CatCanStockSyncService(
      CatCanRepository repository,
      ProductLinkChecker linkChecker,
      @Value("${app.stock-sync.request-delay-ms:1500}") long requestDelayMs) {
    this.repository = repository;
    this.linkChecker = linkChecker;
    this.requestDelayMs = requestDelayMs;
  }

  @Transactional
  public StockSyncSummary syncAll() {
    List<CatCan> items = repository.findAll();
    int checked = 0;
    int skipped = 0;
    int markedInStock = 0;
    int markedOutOfStock = 0;
    int unchanged = 0;

    for (CatCan item : items) {
      ProductLinkCheckResult result = linkChecker.check(item.getShopUrl());
      if (result.skipped()) {
        skipped++;
        log.debug("Skip stock sync for id={}: {}", item.getId(), result.reason());
        sleepBetweenRequests();
        continue;
      }

      checked++;
      boolean wasInStock = item.getInStock() == null || item.getInStock();
      boolean nowInStock = result.inStock();

      if (wasInStock == nowInStock) {
        unchanged++;
      } else if (nowInStock) {
        markedInStock++;
      } else {
        markedOutOfStock++;
      }

      item.setInStock(nowInStock);
      log.info(
          "Stock sync id={} shop={} -> inStock={} ({})",
          item.getId(),
          item.getShopName(),
          nowInStock,
          result.reason());

      sleepBetweenRequests();
    }

    return new StockSyncSummary(
        items.size(), checked, skipped, markedInStock, markedOutOfStock, unchanged);
  }

  private void sleepBetweenRequests() {
    if (requestDelayMs <= 0) {
      return;
    }
    try {
      Thread.sleep(requestDelayMs);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }
}
