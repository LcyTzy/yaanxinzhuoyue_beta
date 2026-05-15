package com.zhantu.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.Result;
import com.zhantu.entity.ServiceOrder;
import com.zhantu.service.ServiceOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/service-order")
@RequiredArgsConstructor
@Tag(name = "管理后台-服务预约管理", description = "服务预约管理接口")
public class AdminServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @GetMapping("/page")
    @Operation(summary = "分页查询预约单")
    public Result<IPage<ServiceOrder>> getServiceOrderPage(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(serviceOrderService.getAdminServiceOrders(status, startDate, endDate, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取预约单详情")
    public Result<ServiceOrder> getServiceOrderDetail(@PathVariable Long id) {
        return Result.success(serviceOrderService.getById(id));
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认预约")
    public Result<Void> confirmServiceOrder(@PathVariable Long id) {
        serviceOrderService.updateServiceOrderStatus(id, "confirmed");
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "完成预约")
    public Result<Void> completeServiceOrder(@PathVariable Long id) {
        serviceOrderService.updateServiceOrderStatus(id, "completed");
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消预约")
    public Result<Void> cancelServiceOrder(@PathVariable Long id) {
        serviceOrderService.updateServiceOrderStatus(id, "cancelled");
        return Result.success();
    }
}
