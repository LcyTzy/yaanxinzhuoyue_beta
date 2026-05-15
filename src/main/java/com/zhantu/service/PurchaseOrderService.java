package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.PurchaseOrder;
import com.zhantu.entity.PurchaseOrderItem;

import java.util.List;

public interface PurchaseOrderService extends IService<PurchaseOrder> {
    IPage<PurchaseOrder> getPurchaseOrderPage(String status, Integer pageNum, Integer pageSize);
    PurchaseOrder getPurchaseOrderDetail(Long id);
    List<PurchaseOrderItem> getItemsByPurchaseOrderId(Long purchaseOrderId);
    PurchaseOrder createPurchaseOrder(Long supplierId, String supplierName, List<PurchaseOrderItem> items, String remark);
    void confirmPurchaseOrder(Long id);
}
