package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.InventoryLog;

public interface InventoryLogService extends IService<InventoryLog> {
    IPage<InventoryLog> getInventoryLogPage(Long productId, String changeType, String startDate, String endDate, Integer pageNum, Integer pageSize);
}
