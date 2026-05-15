package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.logistics.LogisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "物流追踪模块")
@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
public class LogisticsController {

    private final LogisticsService logisticsService;

    @GetMapping("/track")
    @Operation(summary = "查询物流轨迹")
    public Result<Map<String, Object>> track(@RequestParam String company,
                                              @RequestParam String no) {
        return Result.success(logisticsService.queryTracking(company, no));
    }

    @GetMapping("/companies")
    @Operation(summary = "获取支持的快递公司")
    public Result<List<String>> getCompanies() {
        return Result.success(logisticsService.getSupportedCompanies());
    }
}