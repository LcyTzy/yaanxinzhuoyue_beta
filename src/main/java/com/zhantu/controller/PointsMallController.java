package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.entity.PointsExchange;
import com.zhantu.entity.PointsProduct;
import com.zhantu.service.PointsMallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "积分商城模块")
@RestController
@RequestMapping("/api/points-mall")
@RequiredArgsConstructor
public class PointsMallController {

    private final PointsMallService pointsMallService;

    @PostMapping("/sign-in")
    @Operation(summary = "每日签到")
    public Result<Map<String, Object>> signIn(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(pointsMallService.signIn(userId));
    }

    @GetMapping("/sign-status")
    @Operation(summary = "签到状态")
    public Result<Map<String, Object>> getSignInStatus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(pointsMallService.getSignInStatus(userId));
    }

    @GetMapping("/products")
    @Operation(summary = "积分兑换商品列表")
    public Result<List<PointsProduct>> getExchangeProducts() {
        return Result.success(pointsMallService.getExchangeProducts());
    }

    @PostMapping("/exchange/{productId}")
    @Operation(summary = "积分兑换")
    public Result<Void> exchangeProduct(HttpServletRequest request, @PathVariable Long productId) {
        Long userId = (Long) request.getAttribute("userId");
        pointsMallService.exchangeProduct(userId, productId);
        return Result.success();
    }

    @GetMapping("/history")
    @Operation(summary = "兑换记录")
    public Result<List<PointsExchange>> getExchangeHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(pointsMallService.getExchangeHistory(userId));
    }
}