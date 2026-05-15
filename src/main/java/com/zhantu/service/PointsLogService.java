package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.PointsLog;

public interface PointsLogService extends IService<PointsLog> {
    void addPoints(Long userId, Integer points, String type, String description, Long orderId);
    void deductPoints(Long userId, Integer points, String type, String description, Long orderId);
    IPage<PointsLog> getUserPointsLog(Long userId, Integer pageNum, Integer pageSize);
}
