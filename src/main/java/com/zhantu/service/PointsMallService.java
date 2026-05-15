package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.PointsExchange;
import com.zhantu.entity.PointsProduct;
import com.zhantu.entity.SignInRecord;

import java.util.List;
import java.util.Map;

public interface PointsMallService extends IService<PointsProduct> {

    Map<String, Object> signIn(Long userId);

    Map<String, Object> getSignInStatus(Long userId);

    List<PointsProduct> getExchangeProducts();

    void exchangeProduct(Long userId, Long productId);

    List<PointsExchange> getExchangeHistory(Long userId);
}