package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.entity.FlashSale;
import com.zhantu.entity.Promotion;
import com.zhantu.service.FlashSaleService;
import com.zhantu.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "营销活动模块")
@RestController
@RequestMapping("/api/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final FlashSaleService flashSaleService;
    private final PromotionService promotionService;

    @GetMapping("/flash-sales")
    @Operation(summary = "获取进行中的秒杀活动")
    public Result<List<FlashSale>> getFlashSales() {
        return Result.success(flashSaleService.getActiveFlashSales());
    }

    @GetMapping("/list")
    @Operation(summary = "获取进行中的满减活动")
    public Result<List<Promotion>> getPromotions() {
        return Result.success(promotionService.getActivePromotions());
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有活动")
    public Result<Map<String, Object>> getAll() {
        Map<String, Object> result = new HashMap<>();
        result.put("flashSales", flashSaleService.getActiveFlashSales());
        result.put("promotions", promotionService.getActivePromotions());
        return Result.success(result);
    }
}