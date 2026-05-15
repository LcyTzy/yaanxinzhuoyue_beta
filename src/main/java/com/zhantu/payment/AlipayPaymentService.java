package com.zhantu.payment;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AlipayPaymentService implements PaymentService {

    @Value("${alipay.app-id:}")
    private String appId;

    @Value("${alipay.merchant-private-key:}")
    private String merchantPrivateKey;

    @Value("${alipay.alipay-public-key:}")
    private String alipayPublicKey;

    @Value("${alipay.notify-url:}")
    private String notifyUrl;

    @Value("${alipay.return-url:}")
    private String returnUrl;

    @Value("${alipay.sign-type:RSA2}")
    private String signType;

    @Value("${alipay.gateway-url:https://openapi.alipay.com/gateway.do}")
    private String gatewayUrl;

    private AlipayClient alipayClient;

    @PostConstruct
    public void init() {
        if (appId == null || appId.isEmpty() || merchantPrivateKey == null || merchantPrivateKey.isEmpty()) {
            log.warn("支付宝配置不完整，app-id或商户私钥未设置，支付功能不可用");
            return;
        }
        this.alipayClient = new DefaultAlipayClient(
                gatewayUrl, appId, merchantPrivateKey, "json", "UTF-8", alipayPublicKey, signType);
        log.info("支付宝支付服务初始化成功");
    }

    @Override
    public Map<String, Object> createPayment(String orderNo, BigDecimal amount, String subject, String notifyUrlOverride) {
        Map<String, Object> result = new HashMap<>();
        result.put("paymentType", "alipay");
        result.put("orderNo", orderNo);
        result.put("amount", amount);

        if (alipayClient == null) {
            result.put("error", "支付宝未配置");
            return result;
        }

        try {
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(notifyUrlOverride != null ? notifyUrlOverride : notifyUrl);
            request.setReturnUrl(returnUrl);

            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderNo);
            bizContent.put("total_amount", amount.toString());
            bizContent.put("subject", subject);
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
            request.setBizContent(new com.alibaba.fastjson.JSONObject(bizContent).toJSONString());

            String form = alipayClient.pageExecute(request).getBody();
            result.put("payForm", form);
            result.put("payUrl", null);
        } catch (AlipayApiException e) {
            log.error("创建支付宝支付失败: {}", e.getMessage());
            result.put("error", e.getErrMsg());
        }

        return result;
    }

    @Override
    public boolean verifyNotify(Map<String, String> params) {
        if (alipayClient == null) {
            log.warn("支付宝未配置，跳过回调验证");
            return false;
        }
        try {
            boolean verified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", signType);
            if (verified) {
                String tradeStatus = params.get("trade_status");
                return "TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus);
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调验签失败: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public Map<String, Object> queryPayment(String outTradeNo) {
        Map<String, Object> result = new HashMap<>();
        result.put("outTradeNo", outTradeNo);

        if (alipayClient == null) {
            result.put("error", "支付宝未配置");
            return result;
        }

        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", outTradeNo);
            request.setBizContent(new com.alibaba.fastjson.JSONObject(bizContent).toJSONString());

            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                result.put("tradeNo", response.getTradeNo());
                result.put("status", response.getTradeStatus());
                result.put("amount", response.getTotalAmount());
                result.put("buyerLogonId", response.getBuyerLogonId());
            } else {
                result.put("error", response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            log.error("查询支付宝支付状态失败: {}", e.getMessage());
            result.put("error", e.getErrMsg());
        }

        return result;
    }

    @Override
    public Map<String, Object> refund(String outTradeNo, String refundNo, BigDecimal amount, BigDecimal totalAmount) {
        Map<String, Object> result = new HashMap<>();
        result.put("outTradeNo", outTradeNo);
        result.put("refundNo", refundNo);
        result.put("refundAmount", amount);

        if (alipayClient == null) {
            result.put("error", "支付宝未配置");
            return result;
        }

        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", outTradeNo);
            bizContent.put("refund_amount", amount.toString());
            bizContent.put("out_request_no", refundNo);
            request.setBizContent(new com.alibaba.fastjson.JSONObject(bizContent).toJSONString());

            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                result.put("status", "SUCCESS");
                result.put("tradeNo", response.getTradeNo());
            } else {
                result.put("status", "FAIL");
                result.put("error", response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            log.error("支付宝退款失败: {}", e.getMessage());
            result.put("status", "FAIL");
            result.put("error", e.getErrMsg());
        }

        return result;
    }

    @Override
    public String getPaymentType() {
        return "alipay";
    }
}