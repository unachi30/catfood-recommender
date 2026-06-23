-- 刪除重複罐頭資料（保留 id 較小的一筆），並補上 unique index
-- 使用方式：mysql -u root -p catfood < scripts/cleanup-duplicate-cans.sql

DELETE c1
FROM cat_cans c1
INNER JOIN cat_cans c2
  ON c1.brand = c2.brand
 AND c1.name = c2.name
 AND c1.capacity = c2.capacity
 AND c1.shop_name = c2.shop_name
 AND c1.id > c2.id;

ALTER TABLE cat_cans
  ADD UNIQUE KEY unique_product (brand, name, capacity, shop_name);
