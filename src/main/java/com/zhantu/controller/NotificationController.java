package com.zhantu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.Result;
import com.zhantu.entity.Notification;
import com.zhantu.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "消息通知模块")
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/page")
    @Operation(summary = "获取通知列表")
    public Result<IPage<Notification>> getNotifications(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(notificationService.getUserNotifications(userId, pageNum, pageSize));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "获取未读通知数量")
    public Result<Map<String, Object>> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        long count = notificationService.getUnreadCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记已读")
    public Result<Void> markAsRead(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    @PutMapping("/read-all")
    @Operation(summary = "全部标记已读")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAllAsRead(userId);
        return Result.success();
    }
}