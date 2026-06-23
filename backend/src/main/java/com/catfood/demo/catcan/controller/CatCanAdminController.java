package com.catfood.demo.catcan.controller;

import com.catfood.demo.common.ApiResponse;
import com.catfood.demo.catcan.dto.AffiliateShareParseRequestDTO;
import com.catfood.demo.catcan.dto.CatCanBatchApplyRequestDTO;
import com.catfood.demo.catcan.dto.CatCanBatchCheckResultDTO;
import com.catfood.demo.catcan.dto.CatCanBulkCreateRequestDTO;
import com.catfood.demo.catcan.dto.CatCanCreateRequestDTO;
import com.catfood.demo.catcan.dto.CatCanDraftDTO;
import com.catfood.demo.catcan.entity.CatCan;
import com.catfood.demo.catcan.service.CatCanService;
import com.catfood.demo.catcan.service.stock.CatCanStockSyncService;
import com.catfood.demo.catcan.service.stock.StockSyncSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Cat Cans", description = "管理員罐頭商品管理")
@RestController
@RequestMapping("/api/admin/cans")
@PreAuthorize("hasRole('ADMIN')")
public class CatCanAdminController {

  private final CatCanService catCanService;
  private final CatCanStockSyncService stockSyncService;

  public CatCanAdminController(
      CatCanService catCanService, CatCanStockSyncService stockSyncService) {
    this.catCanService = catCanService;
    this.stockSyncService = stockSyncService;
  }

  @Operation(summary = "取得單筆商品", description = "管理員編輯用")
  @GetMapping("/{id}")
  public ApiResponse<CatCan> get(@PathVariable Integer id) {
    return ApiResponse.success("查詢成功", catCanService.findById(id));
  }

  @Operation(summary = "更新罐頭商品", description = "修改各欄位內容")
  @PutMapping("/{id}")
  public ApiResponse<CatCan> update(
      @PathVariable Integer id, @Valid @RequestBody CatCanCreateRequestDTO request) {
    CatCan updated = catCanService.update(id, request);
    return ApiResponse.success("商品已更新", updated);
  }

  @Operation(summary = "解析分享文案", description = "從蝦皮等分享文字擷取連結與商品草稿，需管理員登入")
  @PostMapping("/parse")
  public ApiResponse<CatCanDraftDTO> parseShare(
      @Valid @RequestBody AffiliateShareParseRequestDTO request) {
    CatCanDraftDTO draft = catCanService.parseShareText(request.getShareText());
    return ApiResponse.success("解析完成，請確認欄位後儲存", draft);
  }

  @Operation(summary = "新增罐頭商品", description = "將確認後的商品寫入資料庫，需管理員登入")
  @PostMapping
  public ApiResponse<CatCan> create(@Valid @RequestBody CatCanCreateRequestDTO request) {
    CatCan created = catCanService.create(request);
    return ApiResponse.success("商品已新增", created);
  }

  @Operation(summary = "批次新增多口味商品", description = "共用品牌、名稱、價格等欄位，依口味建立多筆資料")
  @PostMapping("/batch")
  public ApiResponse<List<CatCan>> createBatch(
      @Valid @RequestBody CatCanBulkCreateRequestDTO request) {
    List<CatCan> created = catCanService.createBatch(request);
    return ApiResponse.success("已新增 " + created.size() + " 筆商品", created);
  }

  @Operation(summary = "檢查批次新增衝突", description = "比對品牌、名稱、容量、口味、商城是否已存在於資料庫")
  @PostMapping("/batch/check-conflicts")
  public ApiResponse<CatCanBatchCheckResultDTO> checkBatchConflicts(
      @Valid @RequestBody CatCanBulkCreateRequestDTO request) {
    return ApiResponse.success("衝突檢查完成", catCanService.checkBatchConflicts(request));
  }

  @Operation(summary = "套用批次新增", description = "新增新口味，或依選擇覆蓋既有同口味商品")
  @PostMapping("/batch/apply")
  public ApiResponse<List<CatCan>> applyBatch(
      @Valid @RequestBody CatCanBatchApplyRequestDTO request) {
    List<CatCan> saved = catCanService.applyBatch(request);
    return ApiResponse.success("已處理 " + saved.size() + " 筆商品", saved);
  }

  @Operation(summary = "同步商品連結與庫存", description = "檢查所有購買連結是否有效，無效或頁面顯示無商品時標記為無庫存")
  @PostMapping("/sync-stock")
  public ApiResponse<StockSyncSummary> syncStock() {
    StockSyncSummary summary = stockSyncService.syncAll();
    return ApiResponse.success(
        String.format(
            "已檢查 %d 筆，略過 %d 筆，標記有庫存 %d 筆、無庫存 %d 筆",
            summary.checked(), summary.skipped(), summary.markedInStock(), summary.markedOutOfStock()),
        summary);
  }
}
