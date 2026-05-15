package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Coupon;

import java.util.List;

public interface CouponService extends IService<Coupon> {
    List<Coupon> getAvailableCoupons();
    void receiveCoupon(Long userId, Long couponId);
}
