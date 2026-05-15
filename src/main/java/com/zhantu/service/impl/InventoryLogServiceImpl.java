package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.InventoryLog;
import com.zhantu.mapper.InventoryLogMapper;
import com.zhantu.service.InventoryLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class InventoryLogServiceImpl extends ServiceImpl<InventoryLogMapper, InventoryLog> implements InventoryLogService {

    @Override
    public IPage<InventoryLog> getInventoryLogPage(Long productId, String changeType, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        Page<InventoryLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InventoryLog> wrapper = new LambdaQueryWrapper<>();
        
        if (productId != null) {
            wrapper.eq(InventoryLog::getProductId, productId);
        }
        
        if (StringUtils.hasText(changeType)) {
            wrapper.eq(InventoryLog::getChangeType, changeType);
        }
        
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(InventoryLog::getCreateTime, startDate);
        }
        
        if (StringUtils.hasText(endDate)) {
            wrapper.le(InventoryLog::getCreateTime, endDate);
        }
        
        wrapper.orderByDesc(InventoryLog::getCreateTime);
        
        return this.page(page, wrapper);
    }
}
