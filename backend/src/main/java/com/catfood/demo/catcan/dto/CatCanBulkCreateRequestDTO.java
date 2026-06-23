package com.catfood.demo.catcan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(name = "CatCanBulkCreateRequest", description = "批次新增同系列多口味罐頭")
public class CatCanBulkCreateRequestDTO {

  public static final int MAX_FLAVORS = 20;

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

  @Schema(description = "口味列表（最多 20 種，其餘欄位共用）", example = "[\"鮪魚慕斯\", \"雞肉慕斯\"]")
  @JsonProperty("flavors")
  @NotEmpty(message = "至少需填一種口味")
  @Size(max = MAX_FLAVORS, message = "口味最多 20 種")
  private List<@NotBlank(message = "口味不可空白") @Size(max = 100) String> flavors;

  @Schema(description = "容量", example = "80g")
  @JsonProperty("capacity")
  @NotBlank(message = "容量為必填")
  @Size(max = 50)
  private String capacity;

  @Schema(description = "售價", example = "928")
  @JsonProperty("price")
  @NotNull(message = "價格為必填")
  @Positive(message = "價格須大於 0")
  private Double price;

  @JsonProperty("totalCount")
  private Integer totalCount;

  @JsonProperty("unitPrice")
  private Double unitPrice;

  @JsonProperty("shopName")
  @NotBlank(message = "商城為必填")
  @Size(max = 100)
  private String shopName;

  @JsonProperty("shopUrl")
  @NotBlank(message = "分潤連結為必填")
  @Size(max = 500)
  private String shopUrl;

  @JsonProperty("rating")
  private Double rating;

  @JsonProperty("reviewCount")
  private Integer reviewCount;

  @JsonProperty("description")
  private String description;

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

  public List<String> getFlavors() {
    return flavors;
  }

  public void setFlavors(List<String> flavors) {
    this.flavors = flavors;
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
