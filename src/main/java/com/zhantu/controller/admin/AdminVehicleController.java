package com.zhantu.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.Result;
import com.zhantu.entity.VehicleBrand;
import com.zhantu.entity.VehicleModel;
import com.zhantu.entity.VehicleSeries;
import com.zhantu.service.VehicleBrandService;
import com.zhantu.service.VehicleModelService;
import com.zhantu.service.VehicleSeriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vehicle")
@RequiredArgsConstructor
@Tag(name = "车型数据管理", description = "管理后台车型数据管理接口")
public class AdminVehicleController {

    private final VehicleBrandService vehicleBrandService;
    private final VehicleSeriesService vehicleSeriesService;
    private final VehicleModelService vehicleModelService;

    @GetMapping("/brands")
    @Operation(summary = "获取所有品牌")
    public Result<List<VehicleBrand>> getBrands() {
        return Result.success(vehicleBrandService.list());
    }

    @PostMapping("/brand")
    @Operation(summary = "新增品牌")
    public Result<VehicleBrand> createBrand(@RequestBody VehicleBrand brand) {
        vehicleBrandService.save(brand);
        return Result.success(brand);
    }

    @PutMapping("/brand/{id}")
    @Operation(summary = "更新品牌")
    public Result<Void> updateBrand(@PathVariable Long id, @RequestBody VehicleBrand brand) {
        brand.setId(id);
        vehicleBrandService.updateById(brand);
        return Result.success();
    }

    @DeleteMapping("/brand/{id}")
    @Operation(summary = "删除品牌")
    public Result<Void> deleteBrand(@PathVariable Long id) {
        vehicleBrandService.removeById(id);
        return Result.success();
    }

    @GetMapping("/series")
    @Operation(summary = "获取所有车系")
    public Result<List<VehicleSeries>> getSeries(@RequestParam(required = false) Long brandId) {
        if (brandId != null) {
            return Result.success(vehicleSeriesService.getSeriesByBrandId(brandId));
        }
        return Result.success(vehicleSeriesService.list());
    }

    @PostMapping("/series")
    @Operation(summary = "新增车系")
    public Result<VehicleSeries> createSeries(@RequestBody VehicleSeries series) {
        vehicleSeriesService.save(series);
        return Result.success(series);
    }

    @PutMapping("/series/{id}")
    @Operation(summary = "更新车系")
    public Result<Void> updateSeries(@PathVariable Long id, @RequestBody VehicleSeries series) {
        series.setId(id);
        vehicleSeriesService.updateById(series);
        return Result.success();
    }

    @DeleteMapping("/series/{id}")
    @Operation(summary = "删除车系")
    public Result<Void> deleteSeries(@PathVariable Long id) {
        vehicleSeriesService.removeById(id);
        return Result.success();
    }

    @GetMapping("/models")
    @Operation(summary = "分页查询车型")
    public Result<IPage<VehicleModel>> getModels(
            @RequestParam(required = false) Long seriesId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(vehicleModelService.getModelsPage(seriesId, keyword, pageNum, pageSize));
    }

    @PostMapping("/model")
    @Operation(summary = "新增车型")
    public Result<VehicleModel> createModel(@RequestBody VehicleModel model) {
        vehicleModelService.save(model);
        return Result.success(model);
    }

    @PutMapping("/model/{id}")
    @Operation(summary = "更新车型")
    public Result<Void> updateModel(@PathVariable Long id, @RequestBody VehicleModel model) {
        model.setId(id);
        vehicleModelService.updateById(model);
        return Result.success();
    }

    @DeleteMapping("/model/{id}")
    @Operation(summary = "删除车型")
    public Result<Void> deleteModel(@PathVariable Long id) {
        vehicleModelService.removeById(id);
        return Result.success();
    }

    @GetMapping("/tree")
    @Operation(summary = "获取品牌-车系-车型树")
    public Result<List<VehicleBrand>> getVehicleTree() {
        return Result.success(vehicleBrandService.getVehicleTree());
    }
}
