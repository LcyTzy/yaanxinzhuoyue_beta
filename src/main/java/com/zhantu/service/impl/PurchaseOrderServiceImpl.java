package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.InventoryLog;
import com.zhantu.entity.Product;
import com.zhantu.entity.PurchaseOrder;
import com.zhantu.entity.PurchaseOrderItem;
import com.zhantu.mapper.PurchaseOrderMapper;
import com.zhantu.service.InventoryLogService;
import com.zhantu.service.ProductService;
import com.zhantu.service.PurchaseOrderItemService;
import com.zhantu.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    private final PurchaseOrderItemService purchaseOrderItemService;
    private final ProductService productService;
    private final InventoryLogService inventoryLogService;
    private final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public IPage<PurchaseOrder> getPurchaseOrderPage(String status, Integer pageNum, Integer pageSize) {
        Page<PurchaseOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(PurchaseOrder::getStatus, status);
        }
        
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public PurchaseOrder getPurchaseOrderDetail(Long id) {
        return this.getById(id);
    }

    @Override
    public List<PurchaseOrderItem> getItemsByPurchaseOrderId(Long purchaseOrderId) {
        return purchaseOrderItemService.getItemsByPurchaseOrderId(purchaseOrderId);
    }

    @Override
    @Transactional
    public PurchaseOrder createPurchaseOrder(Long supplierId, String supplierName, List<PurchaseOrderItem> items, String remark) {
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo(generateOrderNo());
        order.setSupplierId(supplierId);
        order.setSupplierName(supplierName);
        order.setStatus("draft");
        order.setRemark(remark);
        order.setTotalAmount(BigDecimal.ZERO);
        
        this.save(order);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderItem item : items) {
            item.setPurchaseOrderId(order.getId());
            
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                item.setProductName(product.getName());
                item.setProductCode(product.getCode());
            }
            
            if (item.getUnitPrice() != null && item.getQuantity() != null) {
                item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                totalAmount = totalAmount.add(item.getTotalPrice());
            }
            
            purchaseOrderItemService.save(item);
        }
        
        order.setTotalAmount(totalAmount);
        this.updateById(order);
        
        return order;
    }

    @Override
    @Transactional
    public void confirmPurchaseOrder(Long id) {
        PurchaseOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException(400, "采购单不存在");
        }
        
        if (!"draft".equals(order.getStatus()) && !"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "只有草稿或待入库状态的采购单才能确认入库");
        }
        
        List<PurchaseOrderItem> items = purchaseOrderItemService.list(
            new LambdaQueryWrapper<PurchaseOrderItem>()
                .eq(PurchaseOrderItem::getPurchaseOrderId, id)
        );
        
        for (PurchaseOrderItem item : items) {
            Product product = productService.getById(item.getProductId());
            if (product == null) {
                throw new BusinessException(400, "商品不存在: " + item.getProductId());
            }
            
            int beforeQuantity = product.getStock() != null ? product.getStock() : 0;
            int afterQuantity = beforeQuantity + item.getQuantity();
            
            product.setStock(afterQuantity);
            productService.updateById(product);
            
            InventoryLog log = new InventoryLog();
            log.setProductId(item.getProductId());
            log.setProductName(item.getProductName());
            log.setChangeType("purchase_in");
            log.setChangeQuantity(item.getQuantity());
            log.setBeforeQuantity(beforeQuantity);
            log.setAfterQuantity(afterQuantity);
            log.setRelatedOrderNo(order.getOrderNo());
            log.setRemark("采购入库");
            inventoryLogService.save(log);
        }
        
        order.setStatus("completed");
        this.updateById(order);
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int seq = counter.getAndIncrement();
        return "PO" + dateStr + String.format("%04d", seq);
    }
}
