package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.entity.RefundOrder;

import java.util.List;

public interface RefundOrderService {
    Long applyRefund(Long userId, Long orderId, String reason, String images);
    IPage<RefundOrder> getUserRefunds(Long userId, Integer pageNum, Integer pageSize);
    RefundOrder getRefundDetail(Long userId, Long refundId);
    void auditRefund(Long refundId, Integer status, String remark);
    void processRefund(Long refundId);
}
