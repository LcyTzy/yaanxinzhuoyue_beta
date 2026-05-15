package com.zhantu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.Result;
import com.zhantu.entity.ProductReview;
import com.zhantu.service.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "商品评价模块")
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @PostMapping("/add")
    @Operation(summary = "添加评价")
    public Result<Void> addReview(HttpServletRequest request,
                                   @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long productId = Long.valueOf(params.get("productId").toString());
        Long orderId = params.get("orderId") != null ? Long.valueOf(params.get("orderId").toString()) : null;
        Integer rating = (Integer) params.get("rating");
        String content = (String) params.get("content");
        String images = (String) params.get("images");
        productReviewService.addReview(userId, productId, orderId, rating, content, images);
        return Result.success();
    }

    @PostMapping("/follow-up")
    @Operation(summary = "追加评价")
    public Result<Void> followUpReview(HttpServletRequest request,
                                        @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long reviewId = Long.valueOf(params.get("reviewId").toString());
        String content = (String) params.get("content");
        productReviewService.followUpReview(userId, reviewId, content);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "商品评价列表")
    public Result<IPage<ProductReview>> getReviewPage(@RequestParam Long productId,
                                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productReviewService.getReviewPage(productId, pageNum, pageSize));
    }

    @GetMapping("/rating/{productId}")
    @Operation(summary = "商品平均评分")
    public Result<Map<String, Object>> getAverageRating(@PathVariable Long productId) {
        double avgRating = productReviewService.getAverageRating(productId);
        long reviewCount = productReviewService.count(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getProductId, productId)
                        .eq(ProductReview::getStatus, 1));
        Map<String, Object> result = new HashMap<>();
        result.put("averageRating", avgRating);
        result.put("reviewCount", reviewCount);
        return Result.success(result);
    }
}
