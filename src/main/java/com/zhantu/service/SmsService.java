package com.zhantu.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    @Value("${sms.aliyun.access-key-id:}")
    private String accessKeyId;

    @Value("${sms.aliyun.access-key-secret:}")
    private String accessKeySecret;

    @Value("${sms.aliyun.sign-name:}")
    private String signName;

    @Value("${sms.aliyun.template-code:}")
    private String templateCode;

    @Value("${sms.aliyun.enabled:false}")
    private boolean enabled;

    public boolean sendSms(String phone, String code) {
        if (!enabled) {
            log.info("短信服务未启用，验证码: {} -> {}", phone, code);
            return true;
        }

        try {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint("dysmsapi.aliyuncs.com");

            Client client = new Client(config);

            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.sendSms(request);

            if ("OK".equals(response.getBody().getCode())) {
                log.info("短信发送成功: {}", phone);
                return true;
            } else {
                log.error("短信发送失败: {} - {}", response.getBody().getCode(), response.getBody().getMessage());
                return false;
            }
        } catch (Exception e) {
            log.error("短信发送异常: {}", e.getMessage(), e);
            return false;
        }
    }
}
