package com.zhantu.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.Result;
import com.zhantu.entity.PurchaseOrder;
import com.zhantu.entity.PurchaseOrderItem;
import com.zhantu.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchase-order")
@RequiredArgsConstructor
@Tag(name = "采购管理", description = "采购单管理接口")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping("/page")
    @Operation(summary = "分页查询采购单")
    public Result<IPage<PurchaseOrder>> getPurchaseOrderPage(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(purchaseOrderService.getPurchaseOrderPage(status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取采购单详情")
    public Result<PurchaseOrder> getPurchaseOrderDetail(@PathVariable Long id) {
        return Result.success(purchaseOrderService.getPurchaseOrderDetail(id));
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "获取采购单明细")
    public Result<List<PurchaseOrderItem>> getPurchaseOrderItems(@PathVariable Long id) {
        return Result.success(purchaseOrderService.getItemsByPurchaseOrderId(id));
    }

    @PostMapping
    @Operation(summary = "创建采购单")
    public Result<PurchaseOrder> createPurchaseOrder(
            @RequestParam Long supplierId,
            @RequestParam String supplierName,
            @RequestBody List<PurchaseOrderItem> items,
            @RequestParam(required = false) String remark) {
        return Result.success(purchaseOrderService.createPurchaseOrder(supplierId, supplierName, items, remark));
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认入库")
    public Result<Void> confirmPurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.confirmPurchaseOrder(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新采购单状态")
    public Result<Void> updatePurchaseOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        PurchaseOrder order = purchaseOrderService.getById(id);
        if (order == null) {
            return Result.error("采购单不存在");
        }
        order.setStatus(status);
        purchaseOrderService.updateById(order);
        return Result.success();
    }
}
