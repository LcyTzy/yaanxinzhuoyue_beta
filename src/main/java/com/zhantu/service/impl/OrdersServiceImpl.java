package com.zhantu.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.*;
import com.zhantu.mapper.OrdersMapper;
import com.zhantu.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    private final CartService cartService;
    private final ProductService productService;
    private final OrderItemService orderItemService;
    private final UserCouponService userCouponService;
    private final CouponService couponService;
    private final PointsLogService pointsLogService;
    private final DistributedLockService distributedLockService;
    private final CacheService cacheService;
    private final NotificationService notificationService;
    private final MembershipService membershipService;
    private final PromotionService promotionService;

    @Override
    @Transactional
    public Long createOrder(Long userId, List<Long> cartIds, String receiverName, String receiverPhone, String receiverAddress, String remark, Long userCouponId) {
        List<Cart> carts = cartService.listByIds(cartIds);
        if (carts.isEmpty()) {
            throw new BusinessException(400, "购物车为空");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Product product = productService.getById(cart.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new BusinessException(400, "商品不存在或已下架: " + cart.getProductId());
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new BusinessException(400, "商品库存不足: " + product.getName());
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }

        BigDecimal couponDiscount = BigDecimal.ZERO;
        if (userCouponId != null) {
            UserCoupon userCoupon = userCouponService.getById(userCouponId);
            if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
                throw new BusinessException(400, "优惠券不存在");
            }
            if (userCoupon.getStatus() != 0) {
                throw new BusinessException(400, "优惠券不可用");
            }
            
            Coupon coupon = couponService.getById(userCoupon.getCouponId());
            if (coupon == null) {
                throw new BusinessException(400, "优惠券信息异常");
            }
            if (totalAmount.compareTo(coupon.getMinAmount()) < 0) {
                throw new BusinessException(400, "订单金额不满足优惠券使用条件");
            }
            
            if (coupon.getType() == 1) {
                couponDiscount = coupon.getDiscountAmount();
            } else if (coupon.getType() == 2) {
                couponDiscount = totalAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscountRate())).setScale(2, RoundingMode.HALF_UP);
            }
            
            if (couponDiscount.compareTo(totalAmount) > 0) {
                couponDiscount = totalAmount;
            }
        }

        BigDecimal payAmount = totalAmount.subtract(couponDiscount);

        BigDecimal memberDiscount = membershipService.getDiscount(userId);
        BigDecimal memberDiscountAmount = BigDecimal.ZERO;
        if (memberDiscount.compareTo(BigDecimal.ONE) < 0) {
            memberDiscountAmount = payAmount.multiply(BigDecimal.ONE.subtract(memberDiscount)).setScale(2, RoundingMode.HALF_UP);
            payAmount = payAmount.subtract(memberDiscountAmount);
        }

        BigDecimal promotionDiscount = promotionService.calculateDiscount(payAmount);
        if (promotionDiscount.compareTo(BigDecimal.ZERO) > 0) {
            payAmount = payAmount.subtract(promotionDiscount);
        }

        Orders order = new Orders();
        order.setOrderNo(IdUtil.fastSimpleUUID());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setCouponDiscount(couponDiscount);
        order.setPayAmount(payAmount);
        order.setUserCouponId(userCouponId);
        order.setStatus(0);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setRemark(remark);
        this.save(order);

        if (userCouponId != null) {
            userCouponService.useCoupon(userId, userCouponId, order.getId());
        }

        for (Cart cart : carts) {
            Product product = productService.getById(cart.getProductId());

            String lockKey = "order:stock:" + product.getId();
            boolean locked = distributedLockService.tryLockWithRetry(lockKey, 10, TimeUnit.SECONDS, 3, 100);
            if (!locked) {
                throw new BusinessException(500, "系统繁忙，请稍后重试");
            }
            try {
                boolean updated = productService.update(new LambdaUpdateWrapper<Product>()
                        .eq(Product::getId, product.getId())
                        .ge(Product::getStock, cart.getQuantity())
                        .setSql("stock = stock - " + cart.getQuantity()));

                if (!updated) {
                    throw new BusinessException(400, "库存不足，下单失败: " + product.getName());
                }
            } finally {
                distributedLockService.releaseLock(lockKey);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductCode(product.getCode());
            orderItem.setProductImage(product.getImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderItemService.save(orderItem);

            cacheService.evictProductCache(product.getId());
        }

        cartService.removeByIds(cartIds);
        return order.getId();
    }

    @Override
    @Transactional
    public void payOrder(Long userId, Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(400, "订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "订单状态异常");
        }
        order.setStatus(1);
        order.setPayTime(java.time.LocalDateTime.now());
        this.updateById(order);

        int points = order.getPayAmount().intValue();
        pointsLogService.addPoints(userId, points, "order", "订单支付获得积分", orderId);
        notificationService.sendOrderNotification(userId, order.getOrderNo(), 1);
        membershipService.upgradeUserLevel(userId);
    }

    @Override
    @Transactional
    public void confirmReceive(Long userId, Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(400, "订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException(400, "只有待收货状态的订单才能确认收货");
        }
        order.setStatus(3);
        this.updateById(order);
        notificationService.sendOrderNotification(userId, order.getOrderNo(), 3);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(400, "订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "只有待付款状态的订单才能取消");
        }
        order.setStatus(4);
        this.updateById(order);
        notificationService.sendOrderNotification(userId, order.getOrderNo(), 4);

        List<OrderItem> items = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            productService.update(new LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, item.getProductId())
                    .setSql("stock = stock + " + item.getQuantity()));
            cacheService.evictProductCache(item.getProductId());
        }
    }

    @Override
    @Transactional
    public void shipOrder(Long userId, Long orderId, String logisticsCompany, String logisticsNo) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(400, "只有待发货状态的订单才能发货");
        }
        order.setStatus(2);
        order.setLogisticsCompany(logisticsCompany);
        order.setLogisticsNo(logisticsNo);
        order.setShipTime(java.time.LocalDateTime.now());
        this.updateById(order);
        notificationService.sendOrderNotification(order.getUserId(), order.getOrderNo(), 2);
    }

    @Override
    public IPage<Orders> getOrderPage(Long userId, Integer pageNum, Integer pageSize, Integer status) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        IPage<Orders> result = this.page(page, wrapper);

        List<Long> orderIds = result.getRecords().stream()
                .map(Orders::getId).collect(java.util.stream.Collectors.toList());
        if (!orderIds.isEmpty()) {
            List<OrderItem> allItems = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, orderIds)
            );
            java.util.Map<Long, List<OrderItem>> itemsMap = allItems.stream()
                    .collect(java.util.stream.Collectors.groupingBy(OrderItem::getOrderId));
            result.getRecords().forEach(order -> {
                order.setItems(itemsMap.getOrDefault(order.getId(), java.util.Collections.emptyList()));
            });
        }

        return result;
    }

    @Override
    public Orders getOrderDetail(Long userId, Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(400, "订单不存在");
        }
        List<OrderItem> items = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        order.setItems(items);
        return order;
    }

    @Override
    @Transactional
    public void paySuccess(String orderNo) {
        Orders order = getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        if (order.getStatus() != 0) {
            return;
        }
        order.setStatus(1);
        order.setPayTime(java.time.LocalDateTime.now());
        this.updateById(order);

        int points = order.getPayAmount().intValue();
        pointsLogService.addPoints(order.getUserId(), points, "order", "订单支付获得积分", order.getId());
        notificationService.sendOrderNotification(order.getUserId(), order.getOrderNo(), 1);
        membershipService.upgradeUserLevel(order.getUserId());
    }

    @Override
    public Orders getByOrderNo(String orderNo) {
        return this.getOne(new LambdaQueryWrapper<Orders>().eq(Orders::getOrderNo, orderNo));
    }
}
