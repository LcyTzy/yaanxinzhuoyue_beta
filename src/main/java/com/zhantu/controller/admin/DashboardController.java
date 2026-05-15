package com.zhantu.controller.admin;

import com.zhantu.common.Result;
import com.zhantu.entity.Orders;
import com.zhantu.entity.Product;
import com.zhantu.entity.PurchaseOrder;
import com.zhantu.service.OrdersService;
import com.zhantu.service.ProductService;
import com.zhantu.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "仪表盘", description = "管理后台仪表盘接口")
public class DashboardController {

    private final ProductService productService;
    private final PurchaseOrderService purchaseOrderService;
    private final OrdersService ordersService;

    @GetMapping("/stats")
    @Operation(summary = "获取仪表盘统计数据")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        long productCount = productService.count();
        stats.put("productCount", productCount);

        List<Product> allProducts = productService.list();
        long totalStock = allProducts.stream()
                .mapToLong(p -> p.getStock() != null ? p.getStock() : 0)
                .sum();
        stats.put("inventoryCount", totalStock);

        long lowStockCount = allProducts.stream()
                .filter(p -> p.getStock() != null && p.getStock() < 10)
                .count();
        stats.put("lowInventoryCount", lowStockCount);

        long purchaseOrderCount = purchaseOrderService.count();
        stats.put("purchaseOrderCount", purchaseOrderCount);

        long salesOrderCount = ordersService.count();
        stats.put("salesOrderCount", salesOrderCount);

        return Result.success(stats);
    }

    @GetMapping("/recent-orders")
    @Operation(summary = "获取最近订单")
    public Result<List<Map<String, Object>>> getRecentOrders() {
        List<Map<String, Object>> result = new ArrayList<>();

        List<PurchaseOrder> purchaseOrders = purchaseOrderService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PurchaseOrder>()
                        .orderByDesc(PurchaseOrder::getCreateTime)
                        .last("LIMIT 5")
        );

        for (PurchaseOrder po : purchaseOrders) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", po.getId());
            item.put("orderNo", po.getOrderNo());
            item.put("type", "采购");
            item.put("supplierName", po.getSupplierName());
            item.put("totalAmount", po.getTotalAmount());
            item.put("status", po.getStatus());
            item.put("createTime", po.getCreateTime());
            result.add(item);
        }

        List<Orders> salesOrders = ordersService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Orders>()
                        .orderByDesc(Orders::getCreateTime)
                        .last("LIMIT 5")
        );

        for (Orders so : salesOrders) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", so.getId());
            item.put("orderNo", so.getOrderNo());
            item.put("type", "销售");
            item.put("receiverName", so.getReceiverName());
            item.put("totalAmount", so.getTotalAmount());
            item.put("status", so.getStatus());
            item.put("createTime", so.getCreateTime());
            result.add(item);
        }

        result.sort((a, b) -> {
            java.time.LocalDateTime ta = (java.time.LocalDateTime) a.get("createTime");
            java.time.LocalDateTime tb = (java.time.LocalDateTime) b.get("createTime");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

        if (result.size() > 5) {
            result = result.subList(0, 5);
        }

        return Result.success(result);
    }
}