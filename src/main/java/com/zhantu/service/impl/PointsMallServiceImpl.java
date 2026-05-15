package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.*;
import com.zhantu.mapper.PointsProductMapper;
import com.zhantu.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PointsMallServiceImpl extends ServiceImpl<PointsProductMapper, PointsProduct> implements PointsMallService {

    private final SignInRecordService signInRecordService;
    private final PointsExchangeService pointsExchangeService;
    private final UserService userService;
    private final PointsLogService pointsLogService;

    @Override
    @Transactional
    public Map<String, Object> signIn(Long userId) {
        LocalDate today = LocalDate.now();

        SignInRecord existing = signInRecordService.getOne(new LambdaQueryWrapper<SignInRecord>()
                .eq(SignInRecord::getUserId, userId)
                .eq(SignInRecord::getSignDate, today));
        if (existing != null) {
            throw new BusinessException(400, "今日已签到");
        }

        LocalDate yesterday = today.minusDays(1);
        SignInRecord yesterdayRecord = signInRecordService.getOne(new LambdaQueryWrapper<SignInRecord>()
                .eq(SignInRecord::getUserId, userId)
                .eq(SignInRecord::getSignDate, yesterday));

        int consecutiveDays = 1;
        if (yesterdayRecord != null) {
            consecutiveDays = yesterdayRecord.getConsecutiveDays() + 1;
        }

        int points = 5;
        if (consecutiveDays >= 7) points = 20;
        else if (consecutiveDays >= 3) points = 10;

        SignInRecord record = new SignInRecord();
        record.setUserId(userId);
        record.setSignDate(today);
        record.setPoints(points);
        record.setConsecutiveDays(consecutiveDays);
        signInRecordService.save(record);

        pointsLogService.addPoints(userId, points, "sign_in", "每日签到", null);

        Map<String, Object> result = new HashMap<>();
        result.put("points", points);
        result.put("consecutiveDays", consecutiveDays);
        result.put("totalPoints", userService.getById(userId).getPoints());
        return result;
    }

    @Override
    public Map<String, Object> getSignInStatus(Long userId) {
        LocalDate today = LocalDate.now();
        SignInRecord todayRecord = signInRecordService.getOne(new LambdaQueryWrapper<SignInRecord>()
                .eq(SignInRecord::getUserId, userId)
                .eq(SignInRecord::getSignDate, today));

        LocalDate yesterday = today.minusDays(1);
        SignInRecord yesterdayRecord = signInRecordService.getOne(new LambdaQueryWrapper<SignInRecord>()
                .eq(SignInRecord::getUserId, userId)
                .eq(SignInRecord::getSignDate, yesterday));

        Map<String, Object> result = new HashMap<>();
        result.put("signedToday", todayRecord != null);
        result.put("consecutiveDays", yesterdayRecord != null ? yesterdayRecord.getConsecutiveDays() : 0);
        result.put("totalPoints", userService.getById(userId).getPoints());
        return result;
    }

    @Override
    public List<PointsProduct> getExchangeProducts() {
        LambdaQueryWrapper<PointsProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsProduct::getStatus, 1)
                .gt(PointsProduct::getStock, 0)
                .orderByAsc(PointsProduct::getPoints);
        return list(wrapper);
    }

    @Override
    @Transactional
    public void exchangeProduct(Long userId, Long productId) {
        PointsProduct product = getById(productId);
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException(400, "兑换商品不存在或已下架");
        }
        if (product.getStock() <= 0) {
            throw new BusinessException(400, "兑换商品库存不足");
        }

        User user = userService.getById(userId);
        if (user.getPoints() < product.getPoints()) {
            throw new BusinessException(400, "积分不足");
        }

        product.setStock(product.getStock() - 1);
        updateById(product);

        pointsLogService.addPoints(userId, -product.getPoints(), "exchange", "积分兑换: " + product.getName(), null);

        PointsExchange exchange = new PointsExchange();
        exchange.setUserId(userId);
        exchange.setProductId(productId);
        exchange.setProductName(product.getName());
        exchange.setPoints(product.getPoints());
        exchange.setStatus(0);
        pointsExchangeService.save(exchange);
    }

    @Override
    public List<PointsExchange> getExchangeHistory(Long userId) {
        LambdaQueryWrapper<PointsExchange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsExchange::getUserId, userId)
                .orderByDesc(PointsExchange::getCreateTime);
        return pointsExchangeService.list(wrapper);
    }
}