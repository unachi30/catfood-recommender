package com.catfood.demo.catcan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "CatCanCreateRequest", description = "新增罐頭商品")
public class CatCanCreateRequestDTO {

  @Schema(description = "種類", example = "主食罐")
  @JsonProperty("category")
  @NotBlank(message = "種類為必填")
  @Size(max = 50)
  private String category;

  @Schema(description = "品牌", example = "Cherie 法麗")
  @JsonProperty("brand")
  @NotBlank(message = "品牌為必填")
  @Size(max = 100)
  private String brand;

  @Schema(description = "商品名稱", example = "全照護主食罐")
  @JsonProperty("name")
  @NotBlank(message = "名稱為必填")
  @Size(max = 255)
  private String name;

  @Schema(description = "口味")
  @JsonProperty("flavor")
  @Size(max = 100)
  private String flavor;

  @Schema(description = "容量", example = "80g")
  @JsonProperty("capacity")
  @NotBlank(message = "容量為必填")
  @Size(max = 50)
  private String capacity;

  @Schema(description = "售價（組合總價或單罐價）", example = "928")
  @JsonProperty("price")
  @NotNull(message = "價格為必填")
  @Positive(message = "價格須大於 0")
  private Double price;

  @Schema(description = "罐數（組合包）", example = "24")
  @JsonProperty("totalCount")
  private Integer totalCount;

  @Schema(description = "單罐價格")
  @JsonProperty("unitPrice")
  private Double unitPrice;

  @Schema(description = "商城", example = "Shopee")
  @JsonProperty("shopName")
  @NotBlank(message = "商城為必填")
  @Size(max = 100)
  private String shopName;

  @Schema(description = "分潤購買連結")
  @JsonProperty("shopUrl")
  @NotBlank(message = "分潤連結為必填")
  @Size(max = 500)
  private String shopUrl;

  @Schema(description = "評分", example = "4.8")
  @JsonProperty("rating")
  private Double rating;

  @Schema(description = "評論數", example = "120")
  @JsonProperty("reviewCount")
  private Integer reviewCount;

  @Schema(description = "備註說明")
  @JsonProperty("description")
  private String description;

  @Schema(description = "是否有庫存", example = "true")
  @JsonProperty("inStock")
  private Boolean inStock = true;

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFlavor() {
    return flavor;
  }

  public void setFlavor(String flavor) {
    this.flavor = flavor;
  }

  public String getCapacity() {
    return capacity;
  }

  public void setCapacity(String capacity) {
    this.capacity = capacity;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Integer getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  public Double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(Double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopUrl() {
    return shopUrl;
  }

  public void setShopUrl(String shopUrl) {
    this.shopUrl = shopUrl;
  }

  public Double getRating() {
    return rating;
  }

  public void setRating(Double rating) {
    this.rating = rating;
  }

  public Integer getReviewCount() {
    return reviewCount;
  }

  public void setReviewCount(Integer reviewCount) {
    this.reviewCount = reviewCount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getInStock() {
    return inStock;
  }

  public void setInStock(Boolean inStock) {
    this.inStock = inStock;
  }
}
