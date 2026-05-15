package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.model.PartCategoryItem;
import com.zhantu.model.VehicleInfo;
import com.zhantu.service.PartsMatcherService;
import com.zhantu.service.PartsService;
import com.zhantu.service.VehicleBrandService;
import com.zhantu.service.VehicleModelService;
import com.zhantu.service.VehicleSeriesService;
import com.zhantu.service.VinDecoderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    @Autowired
    private VinDecoderManager decoderManager;

    @Autowired
    private PartsMatcherService partsMatcherService;

    @Autowired
    private PartsService partsService;

    @Autowired
    private VehicleBrandService vehicleBrandService;

    @Autowired
    private VehicleSeriesService vehicleSeriesService;

    @Autowired
    private VehicleModelService vehicleModelService;

    @GetMapping("/decode")
    public Result<Map<String, Object>> decodeVin(@RequestParam String vin,
                                                  @RequestParam(required = false, defaultValue = "auto") String source) {
        if (vin == null || vin.trim().length() != 17) {
            return Result.error(400, "VIN码必须为17位");
        }

        VehicleInfo vehicleInfo = decoderManager.decode(vin.trim().toUpperCase(), source);
        if (vehicleInfo == null) {
            return Result.error(404, "未查询到该车辆信息");
        }

        List<?> parts = partsMatcherService.getMatchingParts(vehicleInfo);

        Map<String, Object> result = new HashMap<>();
        result.put("vehicle", vehicleInfo);
        result.put("matchedParts", parts);
        result.put("source", vehicleInfo.getSource());
        return Result.success(result);
    }

    @GetMapping("/decode-with-parts")
    public Result<Map<String, Object>> decodeVinWithParts(@RequestParam String vin,
                                                           @RequestParam(required = false, defaultValue = "auto") String source) {
        if (vin == null || vin.trim().length() != 17) {
            return Result.error(400, "VIN码必须为17位");
        }

        VehicleInfo vehicleInfo = decoderManager.decode(vin.trim().toUpperCase(), source);
        if (vehicleInfo == null) {
            return Result.error(404, "未查询到该车辆信息");
        }

        Map<String, List<PartCategoryItem>> categorizedParts = partsService.getCategorizedParts(vehicleInfo);

        Map<String, Object> result = new HashMap<>();
        result.put("vehicle", vehicleInfo);
        result.put("categories", categorizedParts);
        result.put("source", vehicleInfo.getSource());
        return Result.success(result);
    }

    @GetMapping("/sources")
    public Result<List<String>> getSources() {
        return Result.success(decoderManager.getAvailableSources());
    }

    @GetMapping("/vin/{vin}")
    public Result<Map<String, Object>> queryByVin(@PathVariable String vin) {
        if (vin == null || vin.trim().length() != 17) {
            return Result.error(400, "VIN码必须为17位");
        }

        VehicleInfo vehicleInfo = decoderManager.decode(vin.trim().toUpperCase(), "auto");
        if (vehicleInfo == null) {
            return Result.error(404, "未查询到该车辆信息");
        }

        List<?> parts = partsMatcherService.getMatchingParts(vehicleInfo);

        Map<String, Object> result = new HashMap<>();
        result.put("vehicle", vehicleInfo);
        result.put("parts", parts);
        result.put("source", vehicleInfo.getSource());
        return Result.success(result);
    }

    @GetMapping("/brands")
    public Result<List<?>> getBrands() {
        return Result.success(vehicleBrandService.getAllBrands());
    }

    @GetMapping("/series/{brandId}")
    public Result<List<?>> getSeries(@PathVariable Long brandId) {
        return Result.success(vehicleSeriesService.getSeriesByBrandId(brandId));
    }

    @GetMapping("/models/{seriesId}")
    public Result<List<?>> getModels(@PathVariable Long seriesId) {
        return Result.success(vehicleModelService.getModelsBySeriesId(seriesId));
    }
}