package com.catfood.demo.catcan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "CatCan", description = "貓罐頭資料")
@Entity
@Table(
    name = "cat_cans",
    comment = "貓罐頭資料",
    uniqueConstraints =
        @UniqueConstraint(
            name = "unique_product",
            columnNames = {"brand", "name", "capacity", "shop_name", "flavor"}))
@Getter
@Setter
public class CatCan {

    @Schema(description = "罐頭 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(comment = "罐頭 ID")
    private Integer id;

    @Schema(description = "種類", example = "主食罐")
    @Column(comment = "種類")
    private String category;

    @Schema(description = "品牌", example = "汪喵星球")
    @Column(comment = "品牌")
    private String brand;

    @Schema(description = "商品名稱", example = "無穀雞肉罐")
    @Column(comment = "商品名稱")
    private String name;

    @Schema(description = "口味", example = "雞肉")
    @Column(comment = "口味")
    private String flavor;

    @Schema(description = "容量", example = "80g")
    @Column(comment = "容量")
    private String capacity;

    @Schema(description = "價格", example = "45.0")
    @Column(comment = "價格")
    private Double price;

    @Schema(description = "總數量", example = "24")
    @Column(name = "total_count", comment = "總數量")
    private Integer totalCount;

    @Schema(description = "單價", example = "1.88")
    @Column(name = "unit_price", comment = "單價")
    private Double unitPrice;

    @Schema(description = "商城名稱", example = "蝦皮")
    @Column(name = "shop_name", comment = "商城名稱")
    private String shopName;

    @Schema(description = "商城連結")
    @Column(name = "shop_url", comment = "商城連結")
    private String shopUrl;

    @Schema(description = "是否有庫存", example = "true")
    @Column(name = "in_stock", comment = "是否有庫存")
    private Boolean inStock;

    @Schema(description = "評分", example = "4.5")
    @Column(comment = "評分")
    private Double rating;

    @Schema(description = "評論數", example = "128")
    @Column(name = "review_count", comment = "評論數")
    private Integer reviewCount;

    @Schema(description = "商品描述")
    @Column(comment = "商品描述")
    private String description;
}
