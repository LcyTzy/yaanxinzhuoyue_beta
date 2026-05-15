package com.zhantu.controller.admin;

import com.zhantu.common.Result;
import com.zhantu.entity.VehiclePartRelation;
import com.zhantu.service.VehiclePartRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vehicle/part-relation")
@RequiredArgsConstructor
@Tag(name = "配件车型关联管理", description = "管理后台配件车型关联接口")
public class AdminPartRelationController {

    private final VehiclePartRelationService vehiclePartRelationService;

    @GetMapping("/model/{modelId}")
    @Operation(summary = "获取车型关联的配件列表")
    public Result<List<VehiclePartRelation>> getPartsByModelId(@PathVariable Long modelId) {
        return Result.success(vehiclePartRelationService.getPartsByModelId(modelId));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "获取配件关联的车型ID列表")
    public Result<List<Long>> getModelsByProductId(@PathVariable Long productId) {
        return Result.success(vehiclePartRelationService.getProductIdsByModelId(productId));
    }

    @PostMapping
    @Operation(summary = "添加配件车型关联")
    public Result<Void> addRelation(
            @RequestParam Long modelId,
            @RequestParam Long productId,
            @RequestParam(required = false) String position) {
        vehiclePartRelationService.addRelation(modelId, productId, position);
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "删除配件车型关联")
    public Result<Void> removeRelation(
            @RequestParam Long modelId,
            @RequestParam Long productId) {
        vehiclePartRelationService.removeRelation(modelId, productId);
        return Result.success();
    }

    @PostMapping("/batch")
    @Operation(summary = "批量添加配件车型关联")
    public Result<Void> batchAddRelations(
            @RequestParam Long modelId,
            @RequestBody List<Long> productIds) {
        vehiclePartRelationService.batchAddRelations(modelId, productIds);
        return Result.success();
    }
}
