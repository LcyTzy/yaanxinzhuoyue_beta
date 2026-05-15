package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.Promotion;
import com.zhantu.mapper.PromotionMapper;
import com.zhantu.service.PromotionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionServiceImpl extends ServiceImpl<PromotionMapper, Promotion> implements PromotionService {

    @Override
    public List<Promotion> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Promotion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Promotion::getStatus, 1)
                .le(Promotion::getStartTime, now)
                .ge(Promotion::getEndTime, now)
                .orderByDesc(Promotion::getThresholdAmount);
        return list(wrapper);
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal totalAmount) {
        List<Promotion> promotions = getActivePromotions();
        BigDecimal maxDiscount = BigDecimal.ZERO;
        for (Promotion p : promotions) {
            if (totalAmount.compareTo(p.getThresholdAmount()) >= 0) {
                if (p.getDiscountAmount().compareTo(maxDiscount) > 0) {
                    maxDiscount = p.getDiscountAmount();
                }
            }
        }
        return maxDiscount;
    }
}