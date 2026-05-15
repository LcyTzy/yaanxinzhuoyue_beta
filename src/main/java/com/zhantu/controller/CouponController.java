package com.zhantu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.Result;
import com.zhantu.entity.Coupon;
import com.zhantu.entity.UserCoupon;
import com.zhantu.service.CouponService;
import com.zhantu.service.UserCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "优惠券模块")
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @GetMapping("/list")
    @Operation(summary = "获取可领取优惠券列表")
    public Result<List<Coupon>> getAvailableCoupons() {
        return Result.success(couponService.getAvailableCoupons());
    }

    @PostMapping("/receive")
    @Operation(summary = "领取优惠券")
    public Result<Void> receiveCoupon(HttpServletRequest request,
                                       @RequestBody Map<String, Long> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long couponId = params.get("couponId");
        couponService.receiveCoupon(userId, couponId);
        return Result.success();
    }

    @GetMapping("/my")
    @Operation(summary = "我的优惠券列表")
    public Result<IPage<UserCoupon>> getMyCoupons(HttpServletRequest request,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userCouponService.getUserCoupons(userId, status, pageNum, pageSize));
    }
}
