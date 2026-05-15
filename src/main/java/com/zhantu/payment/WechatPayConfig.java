package com.zhantu.payment;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "wechatpay")
public class WechatPayConfig {

    private String mchId;
    private String merchantSerialNumber;
    private String privateKey;
    private String apiV3Key;
    private String appId;
    private String notifyUrl;

    private PrivateKey loadedPrivateKey;
    private boolean available = false;

    @PostConstruct
    public void init() {
        if (mchId == null || mchId.isEmpty()) {
            log.warn("微信支付配置不完整，mch-id未设置");
            return;
        }
        if (apiV3Key == null || apiV3Key.isEmpty()) {
            log.warn("微信支付配置不完整，APIv3密钥未设置");
            return;
        }
        if (privateKey == null || privateKey.isEmpty()) {
            privateKey = loadPrivateKeyFromFile();
        }
        if (privateKey == null || privateKey.isEmpty()) {
            log.warn("微信支付配置不完整，商户私钥未设置");
            return;
        }
        try {
            this.loadedPrivateKey = loadPrivateKey(privateKey);
            this.available = true;
            log.info("微信支付配置初始化成功, mchId={}", mchId);
        } catch (Exception e) {
            log.error("微信支付配置初始化失败", e);
        }
    }

    private String loadPrivateKeyFromFile() {
        try {
            ClassPathResource resource = new ClassPathResource("wechatpay_private_key.pem");
            if (resource.exists()) {
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                log.info("从classpath加载微信支付私钥文件成功");
                return content;
            }
        } catch (Exception e) {
            log.warn("从classpath加载私钥文件失败: {}", e.getMessage());
        }
        try {
            java.io.File file = new java.io.File("wechatpay_private_key.pem");
            if (file.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
                log.info("从外部文件加载微信支付私钥成功");
                return content;
            }
        } catch (Exception e) {
            log.warn("从外部文件加载私钥失败: {}", e.getMessage());
        }
        return null;
    }

    public String sign(String nonceStr, long timestamp, String body) throws Exception {
        String message = nonceStr + "\n" + timestamp + "\n" + body + "\n";
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(loadedPrivateKey);
        sign.update(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sign.sign());
    }

    public String buildAuthorization(String nonceStr, long timestamp, String body) throws Exception {
        String signature = sign(nonceStr, timestamp, body);
        return "WECHATPAY2-SHA256-RSA2048 mchid=\"" + mchId
                + "\",nonce_str=\"" + nonceStr
                + "\",timestamp=\"" + timestamp
                + "\",serial_no=\"" + merchantSerialNumber
                + "\",signature=\"" + signature + "\"";
    }

    public boolean verifySignature(String wechatpaySignature, String wechatpayTimestamp,
                                    String wechatpayNonce, String body) {
        try {
            String expectedSignature = sign(wechatpayNonce, Long.parseLong(wechatpayTimestamp), body);
            return expectedSignature.equals(wechatpaySignature);
        } catch (Exception e) {
            log.error("微信支付签名验证失败", e);
            return false;
        }
    }

    public String decryptAesGcm(String associatedData, String nonce, String ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(apiV3Key.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
        cipher.updateAAD(associatedData.getBytes(StandardCharsets.UTF_8));
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        String key = privateKeyStr
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}