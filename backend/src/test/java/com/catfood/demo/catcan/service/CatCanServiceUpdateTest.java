package com.catfood.demo.catcan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.catfood.demo.catcan.dto.CatCanBulkCreateRequestDTO;
import com.catfood.demo.catcan.dto.CatCanCreateRequestDTO;
import com.catfood.demo.catcan.entity.CatCan;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CatCanServiceUpdateTest {

  @Autowired private CatCanService catCanService;

  @Test
  void updateChangesFields() {
    CatCanBulkCreateRequestDTO create = new CatCanBulkCreateRequestDTO();
    create.setCategory("主食罐");
    create.setBrand("EditBrand");
    create.setName("編輯測試罐");
    create.setFlavors(List.of("鮪魚慕斯"));
    create.setCapacity("80g");
    create.setPrice(500.0);
    create.setTotalCount(10);
    create.setShopName("Shopee");
    create.setShopUrl("https://example.com/old");
    create.setInStock(true);

    CatCan created = catCanService.createBatch(create).get(0);

    CatCanCreateRequestDTO update = new CatCanCreateRequestDTO();
    update.setCategory("主食罐");
    update.setBrand("EditBrand");
    update.setName("編輯測試罐");
    update.setFlavor("鮪魚慕斯");
    update.setCapacity("80g");
    update.setPrice(450.0);
    update.setTotalCount(10);
    update.setShopName("momo");
    update.setShopUrl("https://example.com/new");
    update.setInStock(false);

    CatCan saved = catCanService.update(created.getId(), update);

    assertEquals(450.0, saved.getPrice());
    assertEquals("momo", saved.getShopName());
    assertEquals("https://example.com/new", saved.getShopUrl());
    assertEquals(false, saved.getInStock());
  }
}
