package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.ServiceType;

import java.util.List;

public interface ServiceTypeService extends IService<ServiceType> {
    List<ServiceType> getAvailableServiceTypes();
}
