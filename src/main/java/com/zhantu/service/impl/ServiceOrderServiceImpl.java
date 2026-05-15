package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.ServiceOrder;
import com.zhantu.mapper.ServiceOrderMapper;
import com.zhantu.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements ServiceOrderService {

    private final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public IPage<ServiceOrder> getUserServiceOrders(Long userId, String status, Integer pageNum, Integer pageSize) {
        Page<ServiceOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getUserId, userId);
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(ServiceOrder::getStatus, status);
        }
        
        wrapper.orderByDesc(ServiceOrder::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public IPage<ServiceOrder> getAdminServiceOrders(String status, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        Page<ServiceOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(ServiceOrder::getStatus, status);
        }
        
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(ServiceOrder::getAppointmentTime, startDate);
        }
        
        if (StringUtils.hasText(endDate)) {
            wrapper.le(ServiceOrder::getAppointmentTime, endDate);
        }
        
        wrapper.orderByAsc(ServiceOrder::getAppointmentTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public ServiceOrder createServiceOrder(ServiceOrder serviceOrder) {
        serviceOrder.setOrderNo(generateOrderNo());
        serviceOrder.setStatus("pending");
        this.save(serviceOrder);
        return serviceOrder;
    }

    @Override
    public void updateServiceOrderStatus(Long id, String status) {
        ServiceOrder serviceOrder = this.getById(id);
        if (serviceOrder == null) {
            throw new BusinessException(400, "服务订单不存在");
        }
        serviceOrder.setStatus(status);
        this.updateById(serviceOrder);
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int seq = counter.getAndIncrement();
        return "SO" + dateStr + String.format("%04d", seq);
    }
}
