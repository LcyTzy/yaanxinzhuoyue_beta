package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.Notification;
import com.zhantu.mapper.NotificationMapper;
import com.zhantu.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public void sendNotification(Long userId, String title, String content, Integer type, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        save(notification);
    }

    @Override
    public IPage<Notification> getUserNotifications(Long userId, Integer pageNum, Integer pageSize) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        return count(wrapper);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = getById(notificationId);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(1);
            updateById(notification);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        Notification update = new Notification();
        update.setIsRead(1);
        update(wrapper);
    }

    @Override
    public void sendOrderNotification(Long userId, String orderNo, Integer orderStatus) {
        String title;
        String content;
        switch (orderStatus) {
            case 1:
                title = "订单已支付";
                content = "您的订单 " + orderNo + " 已支付成功，等待发货";
                break;
            case 2:
                title = "订单已发货";
                content = "您的订单 " + orderNo + " 已发货，请注意查收";
                break;
            case 3:
                title = "订单已完成";
                content = "您的订单 " + orderNo + " 已确认收货，感谢您的购买";
                break;
            case 4:
                title = "订单已取消";
                content = "您的订单 " + orderNo + " 已取消";
                break;
            default:
                title = "订单状态更新";
                content = "您的订单 " + orderNo + " 状态已更新";
        }
        sendNotification(userId, title, content, 1, null);
    }

    @Override
    public void sendRefundNotification(Long userId, String orderNo, String status) {
        String title;
        String content;
        switch (status) {
            case "approved":
                title = "退款已通过";
                content = "您的订单 " + orderNo + " 退款申请已通过";
                break;
            case "rejected":
                title = "退款已拒绝";
                content = "您的订单 " + orderNo + " 退款申请已被拒绝";
                break;
            case "completed":
                title = "退款已完成";
                content = "您的订单 " + orderNo + " 退款已完成，金额将退回您的账户";
                break;
            default:
                title = "退款状态更新";
                content = "您的订单 " + orderNo + " 退款状态已更新";
        }
        sendNotification(userId, title, content, 2, null);
    }
}