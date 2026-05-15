package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.PurchaseOrderItem;

import java.util.List;

public interface PurchaseOrderItemService extends IService<PurchaseOrderItem> {
    List<PurchaseOrderItem> getItemsByPurchaseOrderId(Long purchaseOrderId);
}
