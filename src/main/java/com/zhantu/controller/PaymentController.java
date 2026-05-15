package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.entity.Orders;
import com.zhantu.payment.PaymentManager;
import com.zhantu.payment.WechatPaymentService;
import com.zhantu.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Tag(name = "支付模块")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentManager paymentManager;
    private final OrdersService ordersService;
    private final WechatPaymentService wechatPaymentService;

    @Value("${alipay.notify-url:}")
    private String alipayNotifyUrl;

    @Value("${wechatpay.notify-url:}")
    private String wechatpayNotifyUrl;

    @PostMapping("/create")
    @Operation(summary = "创建支付")
    public Result<Map<String, Object>> createPayment(HttpServletRequest request,
                                                      @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        String orderNo = (String) params.get("orderNo");
        String paymentType = (String) params.get("paymentType");

        Orders order = ordersService.getByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error(400, "订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error(400, "订单状态不正确");
        }

        String notifyUrl = "wechat".equals(paymentType) ? wechatpayNotifyUrl : alipayNotifyUrl;
        Map<String, Object> result = paymentManager.createPayment(
                paymentType, orderNo, order.getPayAmount(), "新卓阅汽配-订单" + orderNo, notifyUrl);

        return Result.success(result);
    }

    @PostMapping("/notify/{type}")
    @Operation(summary = "支付回调通知")
    public String paymentNotify(@PathVariable String type, @RequestParam Map<String, String> params) {
        boolean verified = paymentManager.verifyNotify(type, params);
        if (verified) {
            String orderNo = params.getOrDefault("out_trade_no", params.get("outTradeNo"));
            ordersService.paySuccess(orderNo);
            return "success";
        }
        return "fail";
    }

    @PostMapping("/notify/wechat")
    @Operation(summary = "微信支付回调通知")
    public Map<String, Object> wechatPaymentNotify(
            HttpServletRequest request,
            @RequestBody String body) {
        String wechatpaySignature = request.getHeader("Wechatpay-Signature");
        String wechatpayTimestamp = request.getHeader("Wechatpay-Timestamp");
        String wechatpayNonce = request.getHeader("Wechatpay-Nonce");
        String wechatpaySerial = request.getHeader("Wechatpay-Serial");

        log.info("收到微信支付回调: serial={}, timestamp={}", wechatpaySerial, wechatpayTimestamp);

        Map<String, Object> parseResult = wechatPaymentService.parseNotify(
                body, wechatpaySignature, wechatpayTimestamp, wechatpayNonce, wechatpaySerial);

        if (Boolean.TRUE.equals(parseResult.get("verified"))) {
            String outTradeNo = (String) parseResult.get("outTradeNo");
            String tradeState = (String) parseResult.get("tradeState");
            if ("SUCCESS".equals(tradeState)) {
                ordersService.paySuccess(outTradeNo);
                log.info("微信支付回调处理成功: outTradeNo={}", outTradeNo);
            }
            return Map.of("code", "SUCCESS", "message", "OK");
        }

        return Map.of("code", "FAIL", "message", parseResult.get("message"));
    }

    @GetMapping("/query")
    @Operation(summary = "查询支付状态")
    public Result<Map<String, Object>> queryPayment(@RequestParam String orderNo,
                                                     @RequestParam(defaultValue = "alipay") String type) {
        return Result.success(paymentManager.queryPayment(type, orderNo));
    }

    @PostMapping("/refund")
    @Operation(summary = "退款")
    public Result<Map<String, Object>> refund(@RequestBody Map<String, Object> params) {
        String orderNo = (String) params.get("orderNo");
        String paymentType = (String) params.get("paymentType");
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        BigDecimal totalAmount = new BigDecimal(params.getOrDefault("totalAmount", params.get("amount")).toString());
        String refundNo = "RF" + System.currentTimeMillis();

        Map<String, Object> result = paymentManager.refund(paymentType, orderNo, refundNo, amount, totalAmount);
        return Result.success(result);
    }
}