package com.zhantu.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.Result;
import com.zhantu.entity.InventoryLog;
import com.zhantu.service.InventoryLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/inventory")
@RequiredArgsConstructor
@Tag(name = "库存管理", description = "库存流水查询接口")
public class InventoryController {

    private final InventoryLogService inventoryLogService;

    @GetMapping("/log")
    @Operation(summary = "查询库存流水")
    public Result<IPage<InventoryLog>> getInventoryLogPage(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String changeType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(inventoryLogService.getInventoryLogPage(productId, changeType, startDate, endDate, pageNum, pageSize));
    }
}
