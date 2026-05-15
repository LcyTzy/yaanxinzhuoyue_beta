package com.zhantu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.annotation.OperationLog;
import com.zhantu.annotation.RateLimit;
import com.zhantu.common.Result;
import com.zhantu.entity.Category;
import com.zhantu.entity.Product;
import com.zhantu.entity.VehicleModel;
import com.zhantu.entity.VehiclePartRelation;
import com.zhantu.service.CacheService;
import com.zhantu.service.CategoryService;
import com.zhantu.service.ProductService;
import com.zhantu.service.ProductSkuService;
import com.zhantu.service.VehicleModelService;
import com.zhantu.service.VehiclePartRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "商品模块")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductSkuService productSkuService;
    private final VehiclePartRelationService vehiclePartRelationService;
    private final VehicleModelService vehicleModelService;
    private final CacheService cacheService;

    @GetMapping("/category/tree")
    @Operation(summary = "获取分类树")
    public Result<List<Category>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询商品")
    public Result<IPage<Product>> getProductPage(@RequestParam(required = false) Long categoryId,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) String brand,
                                                   @RequestParam(required = false) Long vehicleModelId,
                                                   @RequestParam(required = false) String sortBy,
                                                   @RequestParam(required = false, defaultValue = "desc") String sortOrder,
                                                   @RequestParam(required = false) java.math.BigDecimal minPrice,
                                                   @RequestParam(required = false) java.math.BigDecimal maxPrice,
                                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productService.getProductPage(categoryId, keyword, brand, vehicleModelId, sortBy, sortOrder, minPrice, maxPrice, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "商品详情")
    public Result<Map<String, Object>> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("product", product);
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<VehiclePartRelation> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(VehiclePartRelation::getProductId, id);
        List<VehiclePartRelation> productRelations = vehiclePartRelationService.list(wrapper);
        
        List<Map<String, Object>> applicableVehicles = productRelations.stream().map(r -> {
            VehicleModel model = vehicleModelService.getById(r.getVehicleModelId());
            if (model != null) {
                Map<String, Object> vehicleInfo = new java.util.HashMap<>();
                vehicleInfo.put("id", model.getId());
                vehicleInfo.put("name", model.getName());
                vehicleInfo.put("year", model.getYear());
                vehicleInfo.put("displacement", model.getDisplacement());
                vehicleInfo.put("position", r.getPosition());
                return vehicleInfo;
            }
            return null;
        }).filter(v -> v != null).collect(Collectors.toList());
        
        result.put("applicableVehicles", applicableVehicles);
        return Result.success(result);
    }

    @GetMapping("/hot")
    @Operation(summary = "热门商品")
    public Result<IPage<Product>> getHotProducts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productService.getHotProducts(pageNum, pageSize));
    }

    @GetMapping("/search/hot")
    @Operation(summary = "搜索热词")
    public Result<Set<String>> getSearchHotList(@RequestParam(defaultValue = "10") int topN) {
        return Result.success(cacheService.getSearchHotList(topN));
    }

    @GetMapping("/search/history")
    @Operation(summary = "搜索历史")
    public Result<List<String>> getSearchHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(cacheService.getSearchHistory(userId));
    }

    @DeleteMapping("/search/history")
    @Operation(summary = "清除搜索历史")
    @OperationLog(module = "搜索", operation = "清除搜索历史")
    public Result<Void> clearSearchHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cacheService.clearSearchHistory(userId);
        return Result.success();
    }

    @GetMapping("/{productId}/skus")
    @Operation(summary = "获取商品SKU")
    public Result<List<com.zhantu.entity.ProductSku>> getSkus(@PathVariable Long productId) {
        return Result.success(productSkuService.getSkusByProductId(productId));
    }
}
