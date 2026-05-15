package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.Coupon;
import com.zhantu.entity.UserCoupon;
import com.zhantu.mapper.CouponMapper;
import com.zhantu.service.CouponService;
import com.zhantu.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    private final UserCouponService userCouponService;

    @Override
    public List<Coupon> getAvailableCoupons() {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1)
               .ge(Coupon::getEndTime, LocalDateTime.now())
               .orderByDesc(Coupon::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public void receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = this.getById(couponId);
        if (coupon == null) {
            throw new BusinessException(400, "优惠券不存在");
        }
        if (coupon.getStatus() != 1) {
            throw new BusinessException(400, "优惠券已失效");
        }
        if (LocalDateTime.now().isBefore(coupon.getStartTime()) || LocalDateTime.now().isAfter(coupon.getEndTime())) {
            throw new BusinessException(400, "优惠券不在有效期内");
        }
        if (coupon.getReceiveCount() >= coupon.getTotalCount()) {
            throw new BusinessException(400, "优惠券已领完");
        }

        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
               .eq(UserCoupon::getCouponId, couponId)
               .eq(UserCoupon::getStatus, 0);
        long count = userCouponService.count(wrapper);
        if (count > 0) {
            throw new BusinessException(400, "已领取过该优惠券");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0);
        userCouponService.save(userCoupon);

        coupon.setReceiveCount(coupon.getReceiveCount() + 1);
        this.updateById(coupon);
    }
}
