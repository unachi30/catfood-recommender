-- 在 DBeaver 對 catfood 資料庫執行一次，替既有 cat_cans 表補上中文 COMMENT
-- （重啟後端時 Hibernate 也會嘗試同步，二擇一即可）

ALTER TABLE cat_cans COMMENT = '貓罐頭資料';

ALTER TABLE cat_cans
    MODIFY COLUMN id INT AUTO_INCREMENT COMMENT '罐頭 ID',
    MODIFY COLUMN category VARCHAR(50) NOT NULL COMMENT '種類',
    MODIFY COLUMN brand VARCHAR(100) NOT NULL COMMENT '品牌',
    MODIFY COLUMN name VARCHAR(255) NOT NULL COMMENT '商品名稱',
    MODIFY COLUMN flavor VARCHAR(100) COMMENT '口味',
    MODIFY COLUMN capacity VARCHAR(50) COMMENT '容量',
    MODIFY COLUMN price DECIMAL(10,2) COMMENT '價格',
    MODIFY COLUMN total_count INT COMMENT '總數量',
    MODIFY COLUMN unit_price DECIMAL(10,2) COMMENT '單價',
    MODIFY COLUMN shop_name VARCHAR(100) COMMENT '商城名稱',
    MODIFY COLUMN shop_url VARCHAR(500) COMMENT '商城連結',
    MODIFY COLUMN in_stock TINYINT(1) DEFAULT 1 COMMENT '是否有庫存',
    MODIFY COLUMN rating DECIMAL(3,2) COMMENT '評分',
    MODIFY COLUMN review_count INT COMMENT '評論數',
    MODIFY COLUMN description TEXT COMMENT '商品描述';
