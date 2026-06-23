package com.catfood.demo.catcan.dto;

import com.catfood.demo.catcan.entity.CatCan;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CatCanIncoming", description = "即將寫入的商品資料（單一口味）")
public class CatCanIncomingDTO {

  @JsonProperty("category")
  private String category;

  @JsonProperty("brand")
  private String brand;

  @JsonProperty("name")
  private String name;

  @JsonProperty("flavor")
  private String flavor;

  @JsonProperty("capacity")
  private String capacity;

  @JsonProperty("price")
  private Double price;

  @JsonProperty("totalCount")
  private Integer totalCount;

  @JsonProperty("unitPrice")
  private Double unitPrice;

  @JsonProperty("shopName")
  private String shopName;

  @JsonProperty("shopUrl")
  private String shopUrl;

  @JsonProperty("rating")
  private Double rating;

  @JsonProperty("reviewCount")
  private Integer reviewCount;

  @JsonProperty("description")
  private String description;

  @JsonProperty("inStock")
  private Boolean inStock;

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
