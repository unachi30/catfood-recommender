package com.catfood.demo.catcan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.catfood.demo.catcan.dto.CatCanBatchApplyRequestDTO;
import com.catfood.demo.catcan.dto.CatCanBatchCheckResultDTO;
import com.catfood.demo.catcan.dto.CatCanBulkCreateRequestDTO;
import com.catfood.demo.catcan.dto.CatCanConflictResolutionDTO;
import com.catfood.demo.catcan.entity.CatCan;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CatCanServiceConflictTest {

  @Autowired private CatCanService catCanService;

  @Test
  void checkBatchConflictsIgnoresDifferentShop() {
    CatCanBulkCreateRequestDTO first = baseRequest("ConflictBrand", "衝突測試罐");
    first.setFlavors(List.of("鮪魚慕斯"));
    first.setShopName("Shopee");
    first.setShopUrl("https://example.com/old");
    first.setPrice(928.0);
    first.setTotalCount(24);
    catCanService.createBatch(first);

    CatCanBulkCreateRequestDTO second = baseRequest("Conflict Brand", "衝 突 測 試 罐");
    second.setFlavors(List.of("鮪魚慕斯"));
    second.setShopName("momo");
    second.setShopUrl("https://example.com/new");
    second.setPrice(899.0);
    second.setTotalCount(24);

    CatCanBatchCheckResultDTO check = catCanService.checkBatchConflicts(second);

    assertTrue(check.getConflicts().isEmpty());
    assertEquals(List.of("鮪魚慕斯"), check.getNewFlavors());
  }

  @Test
  void checkBatchConflictsDetectsSameFlavorAndShop() {
    CatCanBulkCreateRequestDTO first = baseRequest("SameShopBrand", "同商城測試罐");
    first.setFlavors(List.of("鮪魚慕斯"));
    first.setShopName("Shopee");
    first.setShopUrl("https://example.com/old");
    first.setPrice(928.0);
    first.setTotalCount(24);
    catCanService.createBatch(first);

    CatCanBulkCreateRequestDTO second = baseRequest("SameShopBrand", "同商城測試罐");
    second.setFlavors(List.of("鮪魚慕斯"));
    second.setShopName("Shopee");
    second.setShopUrl("https://example.com/new");
    second.setPrice(899.0);
    second.setTotalCount(24);

    CatCanBatchCheckResultDTO check = catCanService.checkBatchConflicts(second);

    assertEquals(1, check.getConflicts().size());
    assertEquals("鮪魚慕斯", check.getConflicts().get(0).getFlavor());
    assertEquals("Shopee", check.getConflicts().get(0).getExisting().getShopName());
    assertTrue(check.getNewFlavors().isEmpty());
  }

  @Test
  void applyBatchUseNewReplacesExistingLink() {
    CatCanBulkCreateRequestDTO first = baseRequest("ReplaceBrand", "替換測試罐");
    first.setFlavors(List.of("雞肉慕斯"));
    first.setShopName("Shopee");
    first.setShopUrl("https://example.com/old");
    first.setPrice(500.0);
    first.setTotalCount(10);
    List<CatCan> created = catCanService.createBatch(first);
    Integer existingId = created.get(0).getId();

    CatCanBatchApplyRequestDTO apply = new CatCanBatchApplyRequestDTO();
    apply.setCategory(first.getCategory());
    apply.setBrand(first.getBrand());
    apply.setName(first.getName());
    apply.setFlavors(List.of("雞肉慕斯"));
    apply.setCapacity(first.getCapacity());
    apply.setPrice(450.0);
    apply.setTotalCount(10);
    apply.setShopName("Shopee");
    apply.setShopUrl("https://example.com/cheaper");
    apply.setInStock(true);

    CatCanConflictResolutionDTO resolution = new CatCanConflictResolutionDTO();
    resolution.setFlavor("雞肉慕斯");
    resolution.setUseNew(true);
    apply.setResolutions(List.of(resolution));

    List<CatCan> saved = catCanService.applyBatch(apply);

    assertEquals(1, saved.size());
    assertEquals(existingId, saved.get(0).getId());
    assertEquals("Shopee", saved.get(0).getShopName());
    assertEquals("https://example.com/cheaper", saved.get(0).getShopUrl());
    assertEquals(450.0, saved.get(0).getPrice());
  }

  @Test
  void applyBatchKeepExistingDoesNotChangeRecord() {
    CatCanBulkCreateRequestDTO first = baseRequest("KeepBrand", "保留測試罐");
    first.setFlavors(List.of("南瓜慕斯"));
    first.setShopName("Shopee");
    first.setShopUrl("https://example.com/keep");
    first.setPrice(300.0);
    catCanService.createBatch(first);

    CatCanBatchApplyRequestDTO apply = new CatCanBatchApplyRequestDTO();
    apply.setCategory(first.getCategory());
    apply.setBrand(first.getBrand());
    apply.setName(first.getName());
    apply.setFlavors(List.of("南瓜慕斯"));
    apply.setCapacity(first.getCapacity());
    apply.setPrice(200.0);
    apply.setShopName("Shopee");
    apply.setShopUrl("https://example.com/new");
    apply.setInStock(true);

    CatCanConflictResolutionDTO resolution = new CatCanConflictResolutionDTO();
    resolution.setFlavor("南瓜慕斯");
    resolution.setUseNew(false);
    apply.setResolutions(List.of(resolution));

    List<CatCan> saved = catCanService.applyBatch(apply);
    assertTrue(saved.isEmpty());

  }

  private static CatCanBulkCreateRequestDTO baseRequest(String brand, String name) {
    CatCanBulkCreateRequestDTO request = new CatCanBulkCreateRequestDTO();
    request.setCategory("主食罐");
    request.setBrand(brand);
    request.setName(name);
    request.setCapacity("80g");
    request.setInStock(true);
    return request;
  }
}
