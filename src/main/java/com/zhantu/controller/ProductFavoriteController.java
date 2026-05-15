package com.zhantu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.Result;
import com.zhantu.entity.Product;
import com.zhantu.entity.ProductFavorite;
import com.zhantu.service.ProductFavoriteService;
import com.zhantu.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "商品收藏模块")
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class ProductFavoriteController {

    private final ProductFavoriteService productFavoriteService;
    private final ProductService productService;

    @PostMapping("/toggle")
    @Operation(summary = "切换收藏状态")
    public Result<Void> toggleFavorite(HttpServletRequest request,
                                        @RequestBody Map<String, Long> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long productId = params.get("productId");
        productFavoriteService.toggleFavorite(userId, productId);
        return Result.success();
    }

    @GetMapping("/check")
    @Operation(summary = "检查是否已收藏")
    public Result<Map<String, Boolean>> checkFavorite(HttpServletRequest request,
                                                        @RequestParam Long productId) {
        Long userId = (Long) request.getAttribute("userId");
        boolean favorite = productFavoriteService.isFavorite(userId, productId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("favorite", favorite);
        return Result.success(result);
    }

    @GetMapping("/page")
    @Operation(summary = "收藏列表")
    public Result<IPage<FavoriteVO>> getFavoritePage(HttpServletRequest request,
                                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<ProductFavorite> favoritePage = productFavoriteService.getFavoritePage(userId, pageNum, pageSize);
        
        List<Long> productIds = favoritePage.getRecords().stream()
                .map(ProductFavorite::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productIds.isEmpty() ? java.util.Collections.emptyList() : productService.listByIds(productIds);
        
        IPage<FavoriteVO> voPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                favoritePage.getCurrent(), favoritePage.getSize(), favoritePage.getTotal());
        
        List<FavoriteVO> voList = favoritePage.getRecords().stream().map(fav -> {
            FavoriteVO vo = new FavoriteVO();
            vo.setId(fav.getId());
            vo.setProductId(fav.getProductId());
            vo.setUserId(fav.getUserId());
            vo.setCreateTime(fav.getCreateTime() != null ? fav.getCreateTime().toString() : null);
            
            Product product = products.stream()
                    .filter(p -> p.getId().equals(fav.getProductId()))
                    .findFirst()
                    .orElse(null);
            if (product != null) {
                vo.setProductName(product.getName());
                vo.setProductImage(product.getImage());
                vo.setPrice(product.getPrice() != null ? product.getPrice().doubleValue() : null);
            }
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return Result.success(voPage);
    }

    @GetMapping("/count/{productId}")
    @Operation(summary = "商品收藏数")
    public Result<Map<String, Long>> getFavoriteCount(@PathVariable Long productId) {
        long count = productFavoriteService.getFavoriteCount(productId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    @Data
    public static class FavoriteVO {
        private Long id;
        private Long userId;
        private Long productId;
        private String productName;
        private String productImage;
        private Double price;
        private String createTime;
    }
}
