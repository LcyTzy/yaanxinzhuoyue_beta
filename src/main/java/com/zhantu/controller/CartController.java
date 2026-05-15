package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.dto.CartVO;
import com.zhantu.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车模块")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @Operation(summary = "加入购物车")
    public Result<Void> addToCart(HttpServletRequest request,
                                   @RequestParam Long productId,
                                   @RequestParam(defaultValue = "1") Integer quantity) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.addToCart(userId, productId, quantity);
        return Result.success();
    }

    @PutMapping("/update")
    @Operation(summary = "修改购物车数量")
    public Result<Void> updateCart(HttpServletRequest request,
                                    @RequestParam Long cartId,
                                    @RequestParam Integer quantity) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.updateCart(userId, cartId, quantity);
        return Result.success();
    }

    @DeleteMapping("/{cartId}")
    @Operation(summary = "删除购物车项")
    public Result<Void> deleteCart(HttpServletRequest request,
                                    @PathVariable Long cartId) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.deleteCart(userId, cartId);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "获取购物车列表")
    public Result<List<CartVO>> getCartList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(cartService.getCartList(userId));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "清空购物车")
    public Result<Void> clearCart(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.clearCart(userId);
        return Result.success();
    }
}
