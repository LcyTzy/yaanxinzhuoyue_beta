package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.ServiceOrder;

public interface ServiceOrderService extends IService<ServiceOrder> {
    IPage<ServiceOrder> getUserServiceOrders(Long userId, String status, Integer pageNum, Integer pageSize);
    IPage<ServiceOrder> getAdminServiceOrders(String status, String startDate, String endDate, Integer pageNum, Integer pageSize);
    ServiceOrder createServiceOrder(ServiceOrder serviceOrder);
    void updateServiceOrderStatus(Long id, String status);
}
