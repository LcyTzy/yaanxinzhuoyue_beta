package com.zhantu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.annotation.OperationLog;
import com.zhantu.common.Result;
import com.zhantu.entity.RefundOrder;
import com.zhantu.service.RefundOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "退款模块")
@RestController
@RequestMapping("/api/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundOrderService refundOrderService;

    @PostMapping("/apply")
    @Operation(summary = "申请退款")
    @OperationLog(module = "退款", operation = "申请退款")
    public Result<Long> applyRefund(HttpServletRequest request,
                                     @RequestBody RefundApplyRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        Long refundId = refundOrderService.applyRefund(userId, req.getOrderId(),
                req.getReason(), req.getImages());
        return Result.success(refundId);
    }

    @GetMapping("/page")
    @Operation(summary = "退款记录列表")
    public Result<IPage<RefundOrder>> getRefundPage(HttpServletRequest request,
                                                      @RequestParam(defaultValue = "1") Integer pageNum,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(refundOrderService.getUserRefunds(userId, pageNum, pageSize));
    }

    @GetMapping("/{refundId}")
    @Operation(summary = "退款详情")
    public Result<RefundOrder> getRefundDetail(HttpServletRequest request,
                                                 @PathVariable Long refundId) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(refundOrderService.getRefundDetail(userId, refundId));
    }

    @PostMapping("/admin/audit")
    @Operation(summary = "审核退款(管理员)")
    @OperationLog(module = "退款管理", operation = "审核退款")
    public Result<Void> auditRefund(@RequestParam Long refundId,
                                     @RequestParam Integer status,
                                     @RequestParam(required = false) String remark) {
        refundOrderService.auditRefund(refundId, status, remark);
        return Result.success();
    }

    @PostMapping("/admin/process/{refundId}")
    @Operation(summary = "处理退款(管理员)")
    @OperationLog(module = "退款管理", operation = "处理退款")
    public Result<Void> processRefund(@PathVariable Long refundId) {
        refundOrderService.processRefund(refundId);
        return Result.success();
    }

    public static class RefundApplyRequest {
        private Long orderId;
        private String reason;
        private String images;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getImages() { return images; }
        public void setImages(String images) { this.images = images; }
    }
}
