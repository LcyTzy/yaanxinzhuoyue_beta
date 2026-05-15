package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.service.EpcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/epc")
@RequiredArgsConstructor
@Tag(name = "EPC查询", description = "电子配件目录查询接口")
public class EpcController {

    private final EpcService epcService;

    @GetMapping("/query-by-vin")
    @Operation(summary = "根据VIN码查询车型")
    public Result<Map<String, Object>> queryByVin(@RequestParam String vin) {
        return Result.success(epcService.queryByVin(vin));
    }

    @GetMapping("/parts")
    @Operation(summary = "根据车型ID查询配件")
    public Result<List<Map<String, Object>>> getPartsByModelId(@RequestParam Long modelId) {
        return Result.success(epcService.getPartsByModelId(modelId));
    }

    @GetMapping("/search")
    @Operation(summary = "根据OE号或关键词搜索配件")
    public Result<List<Map<String, Object>>> searchByKeyword(@RequestParam String keyword) {
        return Result.success(epcService.searchByKeyword(keyword));
    }
}
