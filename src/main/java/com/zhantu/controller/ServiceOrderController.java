package com.zhantu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.BusinessException;
import com.zhantu.common.Result;
import com.zhantu.entity.ServiceOrder;
import com.zhantu.entity.ServiceType;
import com.zhantu.service.ServiceOrderService;
import com.zhantu.service.ServiceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-order")
@RequiredArgsConstructor
@Tag(name = "服务预约", description = "服务预约管理接口")
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;
    private final ServiceTypeService serviceTypeService;

    @PostMapping
    @Operation(summary = "创建预约")
    public Result<ServiceOrder> createServiceOrder(@RequestBody ServiceOrder serviceOrder) {
        return Result.success(serviceOrderService.createServiceOrder(serviceOrder));
    }

    @GetMapping("/my")
    @Operation(summary = "查看我的预约")
    public Result<IPage<ServiceOrder>> getMyServiceOrders(
            HttpServletRequest request,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        return Result.success(serviceOrderService.getUserServiceOrders(userId, status, pageNum, pageSize));
    }

    @GetMapping("/types")
    @Operation(summary = "获取服务类型列表")
    public Result<List<ServiceType>> getServiceTypes() {
        return Result.success(serviceTypeService.getAvailableServiceTypes());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新预约状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        serviceOrderService.updateServiceOrderStatus(id, status);
        return Result.success();
    }
}
