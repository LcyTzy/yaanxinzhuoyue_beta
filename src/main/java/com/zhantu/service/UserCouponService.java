package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.UserCoupon;

public interface UserCouponService extends IService<UserCoupon> {
    IPage<UserCoupon> getUserCoupons(Long userId, Integer status, Integer pageNum, Integer pageSize);
    void useCoupon(Long userId, Long userCouponId, Long orderId);
}
