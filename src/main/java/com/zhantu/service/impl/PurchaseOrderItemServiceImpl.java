package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.PurchaseOrderItem;
import com.zhantu.mapper.PurchaseOrderItemMapper;
import com.zhantu.service.PurchaseOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderItemServiceImpl extends ServiceImpl<PurchaseOrderItemMapper, PurchaseOrderItem> implements PurchaseOrderItemService {

    @Override
    public List<PurchaseOrderItem> getItemsByPurchaseOrderId(Long purchaseOrderId) {
        return this.list(new LambdaQueryWrapper<PurchaseOrderItem>()
                .eq(PurchaseOrderItem::getPurchaseOrderId, purchaseOrderId));
    }
}
