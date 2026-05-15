package com.zhantu.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WechatPaymentService implements PaymentService {

    private static final String WECHAT_API_BASE = "https://api.mch.weixin.qq.com";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final WechatPayConfig wechatPayConfig;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    @Override
    public Map<String, Object> createPayment(String orderNo, BigDecimal amount, String subject, String notifyUrl) {
        Map<String, Object> result = new HashMap<>();
        result.put("paymentType", "wechat");

        if (!wechatPayConfig.isAvailable()) {
            result.put("status", "UNAVAILABLE");
            result.put("message", "微信支付未配置");
            return result;
        }

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("appid", wechatPayConfig.getAppId());
            body.put("mchid", wechatPayConfig.getMchId());
            body.put("description", subject);
            body.put("out_trade_no", orderNo);
            body.put("notify_url", notifyUrl);

            Map<String, Object> amountMap = new HashMap<>();
            amountMap.put("total", amount.multiply(new BigDecimal("100")).intValue());
            amountMap.put("currency", "CNY");
            body.put("amount", amountMap);

            String bodyJson = objectMapper.writeValueAsString(body);
            String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            long timestamp = System.currentTimeMillis() / 1000;
            String authorization = wechatPayConfig.buildAuthorization(nonceStr, timestamp, bodyJson);

            Request request = new Request.Builder()
                    .url(WECHAT_API_BASE + "/v3/pay/transactions/native")
                    .post(RequestBody.create(bodyJson, JSON_MEDIA_TYPE))
                    .header("Authorization", authorization)
                    .header("Accept", "application/json")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    JsonNode json = objectMapper.readTree(responseBody);
                    result.put("codeUrl", json.get("code_url").asText());
                    result.put("orderNo", orderNo);
                    result.put("amount", amount);
                    result.put("status", "CREATED");
                    log.info("微信支付Native下单成功: orderNo={}", orderNo);
                } else {
                    log.error("微信支付Native下单失败: orderNo={}, status={}, body={}",
                            orderNo, response.code(), responseBody);
                    result.put("status", "FAILED");
                    result.put("message", responseBody);
                }
            }
        } catch (Exception e) {
            log.error("微信支付Native下单异常: orderNo={}", orderNo, e);
            result.put("status", "FAILED");
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    public boolean verifyNotify(Map<String, String> params) {
        return wechatPayConfig.isAvailable();
    }

    public Map<String, Object> parseNotify(String body, String wechatpaySignature,
                                            String wechatpayTimestamp, String wechatpayNonce,
                                            String wechatpaySerial) {
        Map<String, Object> result = new HashMap<>();

        if (!wechatPayConfig.isAvailable()) {
            result.put("verified", false);
            result.put("message", "微信支付未配置");
            return result;
        }

        try {
            boolean signatureValid = wechatPayConfig.verifySignature(
                    wechatpaySignature, wechatpayTimestamp, wechatpayNonce, body);
            if (!signatureValid) {
                result.put("verified", false);
                result.put("message", "签名验证失败");
                return result;
            }

            JsonNode notifyJson = objectMapper.readTree(body);
            JsonNode resource = notifyJson.get("resource");
            String associatedData = resource.get("associated_data").asText();
            String nonce = resource.get("nonce").asText();
            String ciphertext = resource.get("ciphertext").asText();

            String decrypted = wechatPayConfig.decryptAesGcm(associatedData, nonce, ciphertext);
            JsonNode transaction = objectMapper.readTree(decrypted);

            result.put("verified", true);
            result.put("outTradeNo", transaction.get("out_trade_no").asText());
            result.put("transactionId", transaction.get("transaction_id").asText());
            result.put("tradeState", transaction.get("trade_state").asText());
            if (transaction.has("amount")) {
                result.put("amount", transaction.get("amount").get("total").asInt());
            }
            log.info("微信支付回调验证成功: outTradeNo={}, tradeState={}",
                    result.get("outTradeNo"), result.get("tradeState"));
        } catch (Exception e) {
            log.error("微信支付回调验证失败", e);
            result.put("verified", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> queryPayment(String outTradeNo) {
        Map<String, Object> result = new HashMap<>();

        if (!wechatPayConfig.isAvailable()) {
            result.put("tradeState", "UNAVAILABLE");
            return result;
        }

        try {
            String path = "/v3/pay/transactions/out-trade-no/" + outTradeNo + "?mchid=" + wechatPayConfig.getMchId();
            String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            long timestamp = System.currentTimeMillis() / 1000;
            String authorization = wechatPayConfig.buildAuthorization(nonceStr, timestamp, "");

            Request request = new Request.Builder()
                    .url(WECHAT_API_BASE + path)
                    .get()
                    .header("Authorization", authorization)
                    .header("Accept", "application/json")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    JsonNode json = objectMapper.readTree(responseBody);
                    result.put("outTradeNo", json.get("out_trade_no").asText());
                    result.put("transactionId", json.get("transaction_id").asText());
                    result.put("tradeState", json.get("trade_state").asText());
                    if (json.has("amount")) {
                        result.put("amount", json.get("amount").get("total").asInt());
                    }
                } else {
                    log.error("微信支付查询失败: outTradeNo={}, status={}, body={}",
                            outTradeNo, response.code(), responseBody);
                    result.put("tradeState", "ERROR");
                    result.put("message", responseBody);
                }
            }
        } catch (Exception e) {
            log.error("微信支付查询异常: outTradeNo={}", outTradeNo, e);
            result.put("tradeState", "ERROR");
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> refund(String outTradeNo, String refundNo, BigDecimal amount, BigDecimal totalAmount) {
        Map<String, Object> result = new HashMap<>();

        if (!wechatPayConfig.isAvailable()) {
            result.put("status", "UNAVAILABLE");
            return result;
        }

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("out_trade_no", outTradeNo);
            body.put("out_refund_no", refundNo);
            body.put("reason", "用户申请退款");

            Map<String, Object> amountMap = new HashMap<>();
            amountMap.put("refund", amount.multiply(new BigDecimal("100")).longValue());
            amountMap.put("total", totalAmount.multiply(new BigDecimal("100")).longValue());
            amountMap.put("currency", "CNY");
            body.put("amount", amountMap);

            String bodyJson = objectMapper.writeValueAsString(body);
            String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            long timestamp = System.currentTimeMillis() / 1000;
            String authorization = wechatPayConfig.buildAuthorization(nonceStr, timestamp, bodyJson);

            Request request = new Request.Builder()
                    .url(WECHAT_API_BASE + "/v3/refund/domestic/refunds")
                    .post(RequestBody.create(bodyJson, JSON_MEDIA_TYPE))
                    .header("Authorization", authorization)
                    .header("Accept", "application/json")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    JsonNode json = objectMapper.readTree(responseBody);
                    result.put("outTradeNo", outTradeNo);
                    result.put("refundNo", refundNo);
                    result.put("refundId", json.get("refund_id").asText());
                    result.put("status", json.get("status").asText());
                    result.put("refundAmount", amount);
                    log.info("微信支付退款成功: outTradeNo={}, refundNo={}, status={}",
                            outTradeNo, refundNo, json.get("status").asText());
                } else {
                    log.error("微信支付退款失败: outTradeNo={}, status={}, body={}",
                            outTradeNo, response.code(), responseBody);
                    result.put("status", "FAILED");
                    result.put("message", responseBody);
                }
            }
        } catch (Exception e) {
            log.error("微信支付退款异常: outTradeNo={}", outTradeNo, e);
            result.put("status", "FAILED");
            result.put("message", e.getMessage());
        }

        return result;
    }

    public Map<String, Object> closeOrder(String outTradeNo) {
        Map<String, Object> result = new HashMap<>();

        if (!wechatPayConfig.isAvailable()) {
            result.put("status", "UNAVAILABLE");
            return result;
        }

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("mchid", wechatPayConfig.getMchId());

            String bodyJson = objectMapper.writeValueAsString(body);
            String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            long timestamp = System.currentTimeMillis() / 1000;
            String authorization = wechatPayConfig.buildAuthorization(nonceStr, timestamp, bodyJson);

            Request request = new Request.Builder()
                    .url(WECHAT_API_BASE + "/v3/pay/transactions/out-trade-no/" + outTradeNo + "/close")
                    .post(RequestBody.create(bodyJson, JSON_MEDIA_TYPE))
                    .header("Authorization", authorization)
                    .header("Accept", "application/json")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    result.put("status", "CLOSED");
                    log.info("微信支付关单成功: outTradeNo={}", outTradeNo);
                } else {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    log.error("微信支付关单失败: outTradeNo={}, status={}, body={}",
                            outTradeNo, response.code(), responseBody);
                    result.put("status", "FAILED");
                    result.put("message", responseBody);
                }
            }
        } catch (Exception e) {
            log.error("微信支付关单异常: outTradeNo={}", outTradeNo, e);
            result.put("status", "FAILED");
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    public String getPaymentType() {
        return "wechat";
    }
}