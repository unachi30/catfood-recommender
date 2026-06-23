package com.catfood.demo.catcan.service.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.stock-sync.enabled", havingValue = "true", matchIfMissing = true)
public class CatCanStockSyncScheduler {

  private static final Logger log = LoggerFactory.getLogger(CatCanStockSyncScheduler.class);

  private final CatCanStockSyncService stockSyncService;

  public CatCanStockSyncScheduler(CatCanStockSyncService stockSyncService) {
    this.stockSyncService = stockSyncService;
  }

  @Scheduled(cron = "${app.stock-sync.cron:0 0 3 * * *}", zone = "Asia/Taipei")
  public void runDailyStockSync() {
    log.info("Starting daily product link & stock sync");
    StockSyncSummary summary = stockSyncService.syncAll();
    log.info(
        "Daily stock sync finished: total={} checked={} skipped={} inStock+={} outOfStock+={} unchanged={}",
        summary.total(),
        summary.checked(),
        summary.skipped(),
        summary.markedInStock(),
        summary.markedOutOfStock(),
        summary.unchanged());
  }
}
