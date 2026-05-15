package com.zhantu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.BusinessException;
import com.zhantu.common.Result;
import com.zhantu.entity.Orders;
import com.zhantu.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "订单模块")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrdersService ordersService;

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public Result<Long> createOrder(HttpServletRequest request,
                                     @RequestBody CreateOrderRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        Long orderId = ordersService.createOrder(userId, req.getCartIds(),
                req.getReceiverName(), req.getReceiverPhone(),
                req.getReceiverAddress(), req.getRemark(), req.getUserCouponId());
        return Result.success(orderId);
    }

    @PostMapping("/pay/{orderId}")
    @Operation(summary = "模拟支付")
    public Result<Void> payOrder(HttpServletRequest request,
                                  @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        ordersService.payOrder(userId, orderId);
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "订单列表")
    public Result<IPage<Orders>> getOrderPage(HttpServletRequest request,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(required = false) Integer status) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(ordersService.getOrderPage(userId, pageNum, pageSize, status));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "订单详情")
    public Result<Orders> getOrderDetail(HttpServletRequest request,
                                          @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(ordersService.getOrderDetail(userId, orderId));
    }

    @PutMapping("/{orderId}/confirm")
    @Operation(summary = "确认收货")
    public Result<Void> confirmReceive(HttpServletRequest request,
                                        @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        ordersService.confirmReceive(userId, orderId);
        return Result.success();
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancelOrder(HttpServletRequest request,
                                     @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        ordersService.cancelOrder(userId, orderId);
        return Result.success();
    }

    @PutMapping("/{orderId}/ship")
    @Operation(summary = "发货")
    public Result<Void> shipOrder(HttpServletRequest request,
                                   @PathVariable Long orderId,
                                   @RequestParam(required = false) String logisticsCompany,
                                   @RequestParam(required = false) String logisticsNo) {
        Long userId = (Long) request.getAttribute("userId");
        ordersService.shipOrder(userId, orderId, logisticsCompany, logisticsNo);
        return Result.success();
    }

    @GetMapping("/{orderId}/logistics")
    @Operation(summary = "查询物流信息")
    public Result<Map<String, Object>> getLogistics(HttpServletRequest request,
                                                      @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        Orders order = ordersService.getOrderDetail(userId, orderId);
        if (order.getLogisticsCompany() == null && order.getLogisticsNo() == null) {
            throw new BusinessException(400, "该订单暂无物流信息");
        }
        Map<String, Object> logistics = new HashMap<>();
        logistics.put("orderNo", order.getOrderNo());
        logistics.put("logisticsCompany", order.getLogisticsCompany());
        logistics.put("logisticsNo", order.getLogisticsNo());
        logistics.put("shipTime", order.getShipTime());
        logistics.put("receiverName", order.getReceiverName());
        logistics.put("receiverPhone", order.getReceiverPhone());
        logistics.put("receiverAddress", order.getReceiverAddress());
        logistics.put("status", order.getStatus());
        return Result.success(logistics);
    }

    public static class CreateOrderRequest {
        private List<Long> cartIds;
        private String receiverName;
        private String receiverPhone;
        private String receiverAddress;
        private String remark;
        private Long userCouponId;

        public List<Long> getCartIds() { return cartIds; }
        public void setCartIds(List<Long> cartIds) { this.cartIds = cartIds; }
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
        public String getReceiverAddress() { return receiverAddress; }
        public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public Long getUserCouponId() { return userCouponId; }
        public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
    }
}
