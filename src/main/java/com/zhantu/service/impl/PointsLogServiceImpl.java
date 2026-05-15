package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.PointsLog;
import com.zhantu.entity.User;
import com.zhantu.mapper.PointsLogMapper;
import com.zhantu.service.PointsLogService;
import com.zhantu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointsLogServiceImpl extends ServiceImpl<PointsLogMapper, PointsLog> implements PointsLogService {

    private final UserService userService;

    @Override
    @Transactional
    public void addPoints(Long userId, Integer points, String type, String description, Long orderId) {
        User user = userService.getById(userId);
        if (user != null) {
            user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + points);
            userService.updateById(user);
        }

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setPoints(points);
        log.setType(type);
        log.setDescription(description);
        log.setOrderId(orderId);
        this.save(log);
    }

    @Override
    @Transactional
    public void deductPoints(Long userId, Integer points, String type, String description, Long orderId) {
        User user = userService.getById(userId);
        if (user != null) {
            int currentPoints = user.getPoints() == null ? 0 : user.getPoints();
            user.setPoints(Math.max(0, currentPoints - points));
            userService.updateById(user);
        }

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setPoints(-points);
        log.setType(type);
        log.setDescription(description);
        log.setOrderId(orderId);
        this.save(log);
    }

    @Override
    public IPage<PointsLog> getUserPointsLog(Long userId, Integer pageNum, Integer pageSize) {
        Page<PointsLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PointsLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsLog::getUserId, userId)
               .orderByDesc(PointsLog::getCreateTime);
        return this.page(page, wrapper);
    }
}
