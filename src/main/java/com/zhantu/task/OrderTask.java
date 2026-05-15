package com.zhantu.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhantu.entity.Orders;
import com.zhantu.entity.OrderItem;
import com.zhantu.entity.Product;
import com.zhantu.service.DistributedLockService;
import com.zhantu.service.OrdersService;
import com.zhantu.service.OrderItemService;
import com.zhantu.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTask {

    private final OrdersService ordersService;
    private final OrderItemService orderItemService;
    private final ProductService productService;
    private final DistributedLockService distributedLockService;

    @Value("${order.timeout-cancel-minutes:30}")
    private int timeoutCancelMinutes;

    @Value("${order.auto-confirm-days:7}")
    private int autoConfirmDays;

    @Scheduled(fixedRate = 60000)
    public void cancelTimeoutOrders() {
        String lockKey = "task:cancelTimeoutOrders";
        if (!distributedLockService.tryLock(lockKey, 120, TimeUnit.SECONDS)) {
            log.info("超时订单取消任务正在执行中，跳过");
            return;
        }
        try {
            log.info("开始执行超时订单取消任务");
            LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Orders::getStatus, 0)
                   .lt(Orders::getCreateTime, LocalDateTime.now().minusMinutes(timeoutCancelMinutes));
            List<Orders> timeoutOrders = ordersService.list(wrapper);

            for (Orders order : timeoutOrders) {
                try {
                    order.setStatus(4);
                    ordersService.updateById(order);

                    List<OrderItem> items = orderItemService.list(
                        new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
                    for (OrderItem item : items) {
                        productService.update(new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Product>()
                            .eq(Product::getId, item.getProductId())
                            .setSql("stock = stock + " + item.getQuantity()));
                    }
                    log.info("超时订单已取消: {}", order.getOrderNo());
                } catch (Exception e) {
                    log.error("取消超时订单失败: {}", order.getOrderNo(), e);
                }
            }
            log.info("超时订单取消任务完成，共处理 {} 个订单", timeoutOrders.size());
        } finally {
            distributedLockService.releaseLock(lockKey);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void autoConfirmReceived() {
        String lockKey = "task:autoConfirmReceived";
        if (!distributedLockService.tryLock(lockKey, 120, TimeUnit.SECONDS)) {
            log.info("自动确认收货任务正在执行中，跳过");
            return;
        }
        try {
            log.info("开始执行自动确认收货任务");
            LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Orders::getStatus, 2)
                   .lt(Orders::getShipTime, LocalDateTime.now().minusDays(autoConfirmDays));
            List<Orders> shippedOrders = ordersService.list(wrapper);

            for (Orders order : shippedOrders) {
                try {
                    order.setStatus(3);
                    ordersService.updateById(order);
                    log.info("订单自动确认收货: {}", order.getOrderNo());
                } catch (Exception e) {
                    log.error("自动确认收货失败: {}", order.getOrderNo(), e);
                }
            }
            log.info("自动确认收货任务完成，共处理 {} 个订单", shippedOrders.size());
        } finally {
            distributedLockService.releaseLock(lockKey);
        }
    }
}
