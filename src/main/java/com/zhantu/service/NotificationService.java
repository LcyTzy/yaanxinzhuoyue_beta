package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Notification;

public interface NotificationService extends IService<Notification> {

    void sendNotification(Long userId, String title, String content, Integer type, Long relatedId);

    IPage<Notification> getUserNotifications(Long userId, Integer pageNum, Integer pageSize);

    long getUnreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    void sendOrderNotification(Long userId, String orderNo, Integer orderStatus);

    void sendRefundNotification(Long userId, String orderNo, String status);
}