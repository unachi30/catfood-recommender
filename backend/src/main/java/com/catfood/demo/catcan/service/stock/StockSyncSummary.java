package com.catfood.demo.catcan.service.stock;

public record StockSyncSummary(
    int total,
    int checked,
    int skipped,
    int markedInStock,
    int markedOutOfStock,
    int unchanged) {}
