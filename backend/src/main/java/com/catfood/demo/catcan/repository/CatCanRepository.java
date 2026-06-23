package com.catfood.demo.catcan.repository;

import com.catfood.demo.catcan.entity.CatCan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatCanRepository extends JpaRepository<CatCan, Integer> {

  boolean existsByBrandAndNameAndCapacityAndShopNameAndFlavor(
      String brand, String name, String capacity, String shopName, String flavor);

  boolean existsByBrandAndNameAndCapacityAndShopNameAndFlavorAndIdNot(
      String brand, String name, String capacity, String shopName, String flavor, Integer id);

  List<CatCan> findAllByBrandAndNameAndCapacityAndShopNameAndFlavor(
      String brand, String name, String capacity, String shopName, String flavor);
}
