package com.zhantu.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentManager {

    private final List<PaymentService> paymentServices;

    public PaymentService getPaymentService(String type) {
        return paymentServices.stream()
                .filter(s -> s.getPaymentType().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的支付方式: " + type));
    }

    public Map<String, Object> createPayment(String type, String orderNo, BigDecimal amount, String subject, String notifyUrl) {
        return getPaymentService(type).createPayment(orderNo, amount, subject, notifyUrl);
    }

    public boolean verifyNotify(String type, Map<String, String> params) {
        return getPaymentService(type).verifyNotify(params);
    }

    public Map<String, Object> queryPayment(String type, String outTradeNo) {
        return getPaymentService(type).queryPayment(outTradeNo);
    }

    public Map<String, Object> refund(String type, String outTradeNo, String refundNo, BigDecimal amount, BigDecimal totalAmount) {
        return getPaymentService(type).refund(outTradeNo, refundNo, amount, totalAmount);
    }
}