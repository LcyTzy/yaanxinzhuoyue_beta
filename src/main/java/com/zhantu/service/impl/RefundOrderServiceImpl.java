package com.zhantu.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.Orders;
import com.zhantu.entity.OrderItem;
import com.zhantu.entity.Product;
import com.zhantu.entity.RefundOrder;
import com.zhantu.mapper.RefundOrderMapper;
import com.zhantu.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefundOrderServiceImpl extends ServiceImpl<RefundOrderMapper, RefundOrder> implements RefundOrderService {

    private final OrdersService ordersService;
    private final OrderItemService orderItemService;
    private final ProductService productService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Long applyRefund(Long userId, Long orderId, String reason, String images) {
        Orders order = ordersService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(400, "订单不存在");
        }
        if (order.getStatus() != 1 && order.getStatus() != 2 && order.getStatus() != 3) {
            throw new BusinessException(400, "该订单状态不支持退款");
        }

        long existingCount = this.count(new LambdaQueryWrapper<RefundOrder>()
                .eq(RefundOrder::getOrderId, orderId)
                .eq(RefundOrder::getUserId, userId)
                .ne(RefundOrder::getStatus, 2));
        if (existingCount > 0) {
            throw new BusinessException(400, "该订单已有退款申请在处理中");
        }

        RefundOrder refund = new RefundOrder();
        refund.setRefundNo(IdUtil.fastSimpleUUID());
        refund.setOrderId(orderId);
        refund.setUserId(userId);
        refund.setRefundAmount(order.getPayAmount());
        refund.setRefundReason(reason);
        refund.setRefundImages(images);
        refund.setStatus(0);
        this.save(refund);

        return refund.getId();
    }

    @Override
    public IPage<RefundOrder> getUserRefunds(Long userId, Integer pageNum, Integer pageSize) {
        Page<RefundOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<RefundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RefundOrder::getUserId, userId)
               .orderByDesc(RefundOrder::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public RefundOrder getRefundDetail(Long userId, Long refundId) {
        RefundOrder refund = this.getById(refundId);
        if (refund == null || !refund.getUserId().equals(userId)) {
            throw new BusinessException(400, "退款记录不存在");
        }
        return refund;
    }

    @Override
    @Transactional
    public void auditRefund(Long refundId, Integer status, String remark) {
        RefundOrder refund = this.getById(refundId);
        if (refund == null) {
            throw new BusinessException(400, "退款记录不存在");
        }
        if (refund.getStatus() != 0) {
            throw new BusinessException(400, "该退款已审核");
        }
        refund.setStatus(status);
        refund.setAuditRemark(remark);
        this.updateById(refund);

        Orders order = ordersService.getById(refund.getOrderId());
        if (order != null) {
            String notifStatus = status == 1 ? "approved" : "rejected";
            notificationService.sendRefundNotification(refund.getUserId(), order.getOrderNo(), notifStatus);
        }
    }

    @Override
    @Transactional
    public void processRefund(Long refundId) {
        RefundOrder refund = this.getById(refundId);
        if (refund == null) {
            throw new BusinessException(400, "退款记录不存在");
        }
        if (refund.getStatus() != 1) {
            throw new BusinessException(400, "该退款不可处理，请先审核通过");
        }

        refund.setStatus(3);
        this.updateById(refund);

        Orders order = ordersService.getById(refund.getOrderId());
        if (order != null) {
            order.setStatus(5);
            ordersService.updateById(order);

            List<OrderItem> items = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
            for (OrderItem item : items) {
                productService.update(new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, item.getProductId())
                    .setSql("stock = stock + " + item.getQuantity()));
            }
        }

        refund.setStatus(4);
        refund.setRefundTime(java.time.LocalDateTime.now());
        this.updateById(refund);

        Orders refundOrder = ordersService.getById(refund.getOrderId());
        if (refundOrder != null) {
            notificationService.sendRefundNotification(refund.getUserId(), refundOrder.getOrderNo(), "completed");
        }
    }
}
