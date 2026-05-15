package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Promotion;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionService extends IService<Promotion> {

    List<Promotion> getActivePromotions();

    BigDecimal calculateDiscount(BigDecimal totalAmount);
}