package com.catfood.demo.catcan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.catfood.demo.catcan.dto.CatCanBulkCreateRequestDTO;
import com.catfood.demo.catcan.entity.CatCan;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CatCanServiceBatchTest {

  @Autowired private CatCanService catCanService;

  @Test
  void createBatchWithMultipleFlavors() {
    CatCanBulkCreateRequestDTO request = new CatCanBulkCreateRequestDTO();
    request.setCategory("主食罐");
    request.setBrand("TestBrand");
    request.setName("測試主食罐");
    request.setFlavors(List.of("鮪魚慕斯", "雞肉慕斯"));
    request.setCapacity("80g");
    request.setPrice(928.0);
    request.setTotalCount(24);
    request.setShopName("Shopee");
    request.setShopUrl("https://example.com/p/1");
    request.setInStock(true);

    List<CatCan> created = catCanService.createBatch(request);

    assertEquals(2, created.size());
    assertEquals("鮪魚慕斯", created.get(0).getFlavor());
    assertEquals("雞肉慕斯", created.get(1).getFlavor());
  }

  @Test
  void createBatchStripsWhitespaceFromBrandAndName() {
    CatCanBulkCreateRequestDTO request = new CatCanBulkCreateRequestDTO();
    request.setCategory("主食罐");
    request.setBrand("Whitespace Brand");
    request.setName("空 白 測 試");
    request.setFlavors(List.of("測試口味"));
    request.setCapacity("80g");
    request.setPrice(100.0);
    request.setShopName("TestShop");
    request.setShopUrl("https://example.com/p/whitespace-normalize");
    request.setInStock(true);

    List<CatCan> created = catCanService.createBatch(request);

    assertEquals(1, created.size());
    assertEquals("WhitespaceBrand", created.get(0).getBrand());
    assertEquals("空白測試", created.get(0).getName());
  }
}
