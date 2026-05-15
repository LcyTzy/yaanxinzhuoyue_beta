package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.ServiceType;
import com.zhantu.mapper.ServiceTypeMapper;
import com.zhantu.service.ServiceTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceTypeServiceImpl extends ServiceImpl<ServiceTypeMapper, ServiceType> implements ServiceTypeService {

    @Override
    public List<ServiceType> getAvailableServiceTypes() {
        return this.list(new LambdaQueryWrapper<ServiceType>()
                .eq(ServiceType::getStatus, 1)
                .orderByAsc(ServiceType::getSort));
    }
}
