package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.InsuranceService;

import java.util.List;

public interface InsuranceServiceService extends IService<InsuranceService> {
    void applyService(Long userId, InsuranceService service);
    List<InsuranceService> getUserServices(Long userId);
}