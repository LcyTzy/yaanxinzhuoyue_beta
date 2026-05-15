package com.zhantu.payment;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {

    Map<String, Object> createPayment(String orderNo, BigDecimal amount, String subject, String notifyUrl);

    boolean verifyNotify(Map<String, String> params);

    Map<String, Object> queryPayment(String outTradeNo);

    Map<String, Object> refund(String outTradeNo, String refundNo, BigDecimal amount, BigDecimal totalAmount);

    String getPaymentType();
}