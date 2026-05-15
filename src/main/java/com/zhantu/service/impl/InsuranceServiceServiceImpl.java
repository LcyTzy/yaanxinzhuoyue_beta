package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.InsuranceService;
import com.zhantu.mapper.InsuranceServiceMapper;
import com.zhantu.service.InsuranceServiceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsuranceServiceServiceImpl extends ServiceImpl<InsuranceServiceMapper, InsuranceService> implements InsuranceServiceService {

    @Override
    public void applyService(Long userId, InsuranceService service) {
        service.setUserId(userId);
        service.setStatus(0);
        save(service);
    }

    @Override
    public List<InsuranceService> getUserServices(Long userId) {
        return list(new LambdaQueryWrapper<InsuranceService>()
                .eq(InsuranceService::getUserId, userId)
                .orderByDesc(InsuranceService::getCreateTime));
    }
}