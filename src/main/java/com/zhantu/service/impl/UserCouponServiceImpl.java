package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.Coupon;
import com.zhantu.entity.UserCoupon;
import com.zhantu.mapper.CouponMapper;
import com.zhantu.mapper.UserCouponMapper;
import com.zhantu.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements UserCouponService {

    private final CouponMapper couponMapper;

    @Override
    public IPage<UserCoupon> getUserCoupons(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<UserCoupon> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);

        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }

        wrapper.orderByDesc(UserCoupon::getCreateTime);
        IPage<UserCoupon> result = this.page(page, wrapper);

        List<Long> couponIds = result.getRecords().stream()
                .map(UserCoupon::getCouponId)
                .distinct()
                .collect(Collectors.toList());
        if (!couponIds.isEmpty()) {
            List<Coupon> coupons = couponMapper.selectBatchIds(couponIds);
            Map<Long, Coupon> couponMap = coupons.stream()
                    .collect(Collectors.toMap(Coupon::getId, c -> c));
            result.getRecords().forEach(uc -> {
                Coupon c = couponMap.get(uc.getCouponId());
                if (c != null) {
                    uc.setCouponName(c.getName());
                    uc.setCouponType(c.getType());
                    uc.setCouponDiscountAmount(c.getDiscountAmount());
                    uc.setCouponDiscountRate(c.getDiscountRate());
                    uc.setCouponMinAmount(c.getMinAmount());
                    uc.setCouponStartTime(c.getStartTime());
                    uc.setCouponEndTime(c.getEndTime());
                }
            });
        }

        return result;
    }

    @Override
    public void useCoupon(Long userId, Long userCouponId, Long orderId) {
        UserCoupon userCoupon = this.getById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            throw new BusinessException(400, "优惠券不存在");
        }
        if (userCoupon.getStatus() != 0) {
            throw new BusinessException(400, "优惠券不可用");
        }
        
        userCoupon.setStatus(1);
        userCoupon.setUseTime(LocalDateTime.now());
        userCoupon.setOrderId(orderId);
        this.updateById(userCoupon);
    }
}
