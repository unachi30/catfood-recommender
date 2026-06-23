package com.catfood.demo.catcan.service;

import com.catfood.demo.catcan.dto.CatCanBatchApplyRequestDTO;
import com.catfood.demo.catcan.dto.CatCanBatchCheckResultDTO;
import com.catfood.demo.catcan.dto.CatCanBulkCreateRequestDTO;
import com.catfood.demo.catcan.dto.CatCanConflictResolutionDTO;
import com.catfood.demo.catcan.dto.CatCanCreateRequestDTO;
import com.catfood.demo.catcan.dto.CatCanDraftDTO;
import com.catfood.demo.catcan.dto.CatCanFlavorConflictDTO;
import com.catfood.demo.catcan.dto.CatCanIncomingDTO;
import com.catfood.demo.catcan.entity.CatCan;
import com.catfood.demo.catcan.repository.CatCanRepository;
import com.catfood.demo.catcan.service.parse.AffiliateShareParser;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatCanService {

  private final CatCanRepository repository;
  private final AffiliateShareParser shareParser;

  public CatCanService(CatCanRepository repository, AffiliateShareParser shareParser) {
    this.repository = repository;
    this.shareParser = shareParser;
  }

  public List<CatCan> findAll() {
    return repository.findAll();
  }

  public CatCan findById(Integer id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到商品"));
  }

  @Transactional
  public CatCan update(Integer id, CatCanCreateRequestDTO request) {
    CatCan catCan = findById(id);
    String flavor = trimToNull(request.getFlavor());
    String brand = CatCanTextNormalizer.normalizeBrandOrName(request.getBrand());
    String name = CatCanTextNormalizer.normalizeBrandOrName(request.getName());
    String capacity = request.getCapacity().trim();
    String shopName = request.getShopName().trim();

    if (repository.existsByBrandAndNameAndCapacityAndShopNameAndFlavorAndIdNot(
        brand, name, capacity, shopName, flavor, id)) {
      String flavorLabel = flavor == null ? "（無口味）" : flavor;
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "相同品牌、名稱、容量、商城與口味的商品已存在：" + flavorLabel);
    }

    applyRequestToEntity(catCan, request, flavor);
    try {
      return repository.save(catCan);
    } catch (DataIntegrityViolationException ex) {
      String flavorLabel = flavor == null ? "（無口味）" : flavor;
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "商品資料重複（口味：" + flavorLabel + "），請檢查是否已存在");
    }
  }

  public CatCanDraftDTO parseShareText(String shareText) {
    return shareParser.parse(shareText);
  }

  @Transactional
  public CatCan create(CatCanCreateRequestDTO request) {
    return saveNew(request, trimToNull(request.getFlavor()));
  }

  @Transactional
  public List<CatCan> createBatch(CatCanBulkCreateRequestDTO request) {
    return applyBatch(toApplyRequest(request, List.of()));
  }

  public CatCanBatchCheckResultDTO checkBatchConflicts(CatCanBulkCreateRequestDTO request) {
    NormalizedBatch batch = normalizeBatch(request);
    CatCanBatchCheckResultDTO result = new CatCanBatchCheckResultDTO();

    for (String flavor : batch.flavors()) {
      List<CatCan> existing =
          repository.findAllByBrandAndNameAndCapacityAndShopNameAndFlavor(
              batch.brand(), batch.name(), batch.capacity(), batch.shopName(), flavor);
      if (existing.isEmpty()) {
        result.getNewFlavors().add(flavor);
        continue;
      }

      CatCanFlavorConflictDTO conflict = new CatCanFlavorConflictDTO();
      conflict.setFlavor(flavor);
      conflict.setExisting(existing.get(0));
      conflict.setIncoming(buildIncoming(batch, flavor));
      conflict.setDuplicateCount(existing.size());
      result.getConflicts().add(conflict);
    }

    return result;
  }

  @Transactional
  public List<CatCan> applyBatch(CatCanBatchApplyRequestDTO request) {
    NormalizedBatch batch = normalizeBatch(request);
    Map<String, Boolean> resolutionByFlavor =
        request.getResolutions().stream()
            .collect(
                Collectors.toMap(
                    r -> r.getFlavor().trim(),
                    CatCanConflictResolutionDTO::getUseNew,
                    (a, b) -> b));

    List<CatCan> saved = new ArrayList<>();
    for (String flavor : batch.flavors()) {
      List<CatCan> existing =
          repository.findAllByBrandAndNameAndCapacityAndShopNameAndFlavor(
              batch.brand(), batch.name(), batch.capacity(), batch.shopName(), flavor);

      if (existing.isEmpty()) {
        saved.add(saveNew(toCreateRequest(request), flavor));
        continue;
      }

      Boolean useNew = resolutionByFlavor.get(flavor);
      if (useNew == null) {
        String flavorLabel = flavor == null ? "（無口味）" : flavor;
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "請選擇要保留現有資料或使用新版：" + flavorLabel);
      }
      if (!useNew) {
        continue;
      }

      CatCan target = existing.get(0);
      applyRequestToEntity(target, toCreateRequest(request), flavor);
      saved.add(repository.save(target));

      if (existing.size() > 1) {
        for (int i = 1; i < existing.size(); i++) {
          repository.delete(existing.get(i));
        }
      }
    }

    return saved;
  }

  private static CatCanBatchApplyRequestDTO toApplyRequest(
      CatCanBulkCreateRequestDTO request, List<CatCanConflictResolutionDTO> resolutions) {
    CatCanBatchApplyRequestDTO apply = new CatCanBatchApplyRequestDTO();
    copyBulkFields(request, apply);
    apply.setResolutions(resolutions);
    return apply;
  }

  private static void copyBulkFields(
      CatCanBulkCreateRequestDTO from, CatCanBulkCreateRequestDTO to) {
    to.setCategory(from.getCategory());
    to.setBrand(from.getBrand());
    to.setName(from.getName());
    to.setFlavors(from.getFlavors());
    to.setCapacity(from.getCapacity());
    to.setPrice(from.getPrice());
    to.setTotalCount(from.getTotalCount());
    to.setUnitPrice(from.getUnitPrice());
    to.setShopName(from.getShopName());
    to.setShopUrl(from.getShopUrl());
    to.setRating(from.getRating());
    to.setReviewCount(from.getReviewCount());
    to.setDescription(from.getDescription());
    to.setInStock(from.getInStock());
  }

  private NormalizedBatch normalizeBatch(CatCanBulkCreateRequestDTO request) {
    List<String> flavors = normalizeFlavors(request.getFlavors());
    if (flavors.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "至少需填一種口味");
    }
    if (flavors.size() > CatCanBulkCreateRequestDTO.MAX_FLAVORS) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "口味最多 " + CatCanBulkCreateRequestDTO.MAX_FLAVORS + " 種");
    }

    String brand = CatCanTextNormalizer.normalizeBrandOrName(request.getBrand());
    String name = CatCanTextNormalizer.normalizeBrandOrName(request.getName());
    String capacity = request.getCapacity().trim();
    String shopName = request.getShopName().trim();
    return new NormalizedBatch(brand, name, capacity, shopName, flavors, request);
  }

  private CatCanIncomingDTO buildIncoming(NormalizedBatch batch, String flavor) {
    return toIncoming(toCreateRequest(batch.request()), flavor);
  }

  private void applyRequestToEntity(CatCan catCan, CatCanCreateRequestDTO request, String flavor) {
    String brand = CatCanTextNormalizer.normalizeBrandOrName(request.getBrand());
    String name = CatCanTextNormalizer.normalizeBrandOrName(request.getName());
    String capacity = request.getCapacity().trim();
    int totalCount =
        request.getTotalCount() != null && request.getTotalCount() > 0 ? request.getTotalCount() : 1;

    catCan.setCategory(request.getCategory().trim());
    catCan.setBrand(brand);
    catCan.setName(name);
    catCan.setFlavor(flavor);
    catCan.setCapacity(capacity);
    catCan.setPrice(request.getPrice());
    catCan.setTotalCount(totalCount);
    catCan.setUnitPrice(resolveUnitPrice(request.getPrice(), request.getUnitPrice(), totalCount));
    catCan.setShopName(request.getShopName().trim());
    catCan.setShopUrl(request.getShopUrl().trim());
    catCan.setRating(request.getRating());
    catCan.setReviewCount(request.getReviewCount());
    catCan.setDescription(trimToNull(request.getDescription()));
    catCan.setInStock(request.getInStock() == null || request.getInStock());
  }

  private static CatCanIncomingDTO toIncoming(CatCanCreateRequestDTO request, String flavor) {
    int totalCount =
        request.getTotalCount() != null && request.getTotalCount() > 0 ? request.getTotalCount() : 1;
    CatCanIncomingDTO dto = new CatCanIncomingDTO();
    dto.setCategory(request.getCategory());
    dto.setBrand(CatCanTextNormalizer.normalizeBrandOrName(request.getBrand()));
    dto.setName(CatCanTextNormalizer.normalizeBrandOrName(request.getName()));
    dto.setFlavor(flavor);
    dto.setCapacity(request.getCapacity());
    dto.setPrice(request.getPrice());
    dto.setTotalCount(totalCount);
    dto.setUnitPrice(resolveUnitPrice(request.getPrice(), request.getUnitPrice(), totalCount));
    dto.setShopName(request.getShopName());
    dto.setShopUrl(request.getShopUrl());
    dto.setRating(request.getRating());
    dto.setReviewCount(request.getReviewCount());
    dto.setDescription(request.getDescription());
    dto.setInStock(request.getInStock());
    return dto;
  }

  private record NormalizedBatch(
      String brand,
      String name,
      String capacity,
      String shopName,
      List<String> flavors,
      CatCanBulkCreateRequestDTO request) {}

  private CatCan saveNew(CatCanCreateRequestDTO request, String flavor) {
    String brand = CatCanTextNormalizer.normalizeBrandOrName(request.getBrand());
    String name = CatCanTextNormalizer.normalizeBrandOrName(request.getName());
    String capacity = request.getCapacity().trim();
    String shopName = request.getShopName().trim();

    if (repository.existsByBrandAndNameAndCapacityAndShopNameAndFlavor(
        brand, name, capacity, shopName, flavor)) {
      String flavorLabel = flavor == null ? "（無口味）" : flavor;
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "相同品牌、名稱、容量、商城與口味的商品已存在：" + flavorLabel);
    }

    CatCan catCan = new CatCan();
    catCan.setCategory(request.getCategory().trim());
    catCan.setBrand(brand);
    catCan.setName(name);
    catCan.setFlavor(flavor);
    catCan.setCapacity(capacity);
    catCan.setPrice(request.getPrice());
    int totalCount =
        request.getTotalCount() != null && request.getTotalCount() > 0 ? request.getTotalCount() : 1;
    catCan.setTotalCount(totalCount);
    catCan.setUnitPrice(resolveUnitPrice(request.getPrice(), request.getUnitPrice(), totalCount));
    catCan.setShopName(shopName);
    catCan.setShopUrl(request.getShopUrl().trim());
    catCan.setRating(request.getRating());
    catCan.setReviewCount(request.getReviewCount());
    catCan.setDescription(trimToNull(request.getDescription()));
    catCan.setInStock(request.getInStock() == null || request.getInStock());

    try {
      return repository.save(catCan);
    } catch (DataIntegrityViolationException ex) {
      String flavorLabel = flavor == null ? "（無口味）" : flavor;
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "商品資料重複（口味：" + flavorLabel + "），請檢查是否已新增過");
    }
  }

  private static CatCanCreateRequestDTO toCreateRequest(CatCanBulkCreateRequestDTO request) {
    CatCanCreateRequestDTO dto = new CatCanCreateRequestDTO();
    dto.setCategory(request.getCategory());
    dto.setBrand(request.getBrand());
    dto.setName(request.getName());
    dto.setCapacity(request.getCapacity());
    dto.setPrice(request.getPrice());
    dto.setTotalCount(request.getTotalCount());
    dto.setUnitPrice(request.getUnitPrice());
    dto.setShopName(request.getShopName());
    dto.setShopUrl(request.getShopUrl());
    dto.setRating(request.getRating());
    dto.setReviewCount(request.getReviewCount());
    dto.setDescription(request.getDescription());
    dto.setInStock(request.getInStock());
    return dto;
  }

  private static List<String> normalizeFlavors(List<String> flavors) {
    Set<String> unique = new LinkedHashSet<>();
    for (String raw : flavors) {
      if (raw == null) {
        continue;
      }
      String trimmed = raw.trim();
      if (!trimmed.isEmpty()) {
        unique.add(trimmed);
      }
    }
    return List.copyOf(unique);
  }

  private static Double resolveUnitPrice(Double price, Double unitPrice, int totalCount) {
    if (unitPrice != null) {
      return unitPrice;
    }
    if (price != null && price > 0) {
      return Math.round((price / totalCount) * 100.0) / 100.0;
    }
    return null;
  }

  private static String trimToNull(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
