package com.zhantu.logistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@Component
public class Kuaidi100LogisticsService implements LogisticsService {

    @Value("${kuaidi100.key}")
    private String key;

    @Value("${kuaidi100.customer}")
    private String customer;

    @Value("${kuaidi100.query-url}")
    private String queryUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final Map<String, String> COMPANY_CODE_MAP = new LinkedHashMap<>();

    static {
        COMPANY_CODE_MAP.put("顺丰速运", "shunfeng");
        COMPANY_CODE_MAP.put("中通快递", "zhongtong");
        COMPANY_CODE_MAP.put("圆通速递", "yuantong");
        COMPANY_CODE_MAP.put("韵达快递", "yunda");
        COMPANY_CODE_MAP.put("申通快递", "shentong");
        COMPANY_CODE_MAP.put("京东物流", "jd");
        COMPANY_CODE_MAP.put("EMS", "ems");
        COMPANY_CODE_MAP.put("极兔速递", "jtexpress");
        COMPANY_CODE_MAP.put("德邦快递", "debangwuliu");
        COMPANY_CODE_MAP.put("百世快递", "huitongkuaidi");
        COMPANY_CODE_MAP.put("邮政快递包裹", "youzhengguonei");
        COMPANY_CODE_MAP.put("宅急送", "zhaijisong");
        COMPANY_CODE_MAP.put("天天快递", "tiantian");
        COMPANY_CODE_MAP.put("优速快递", "youshuwuliu");
        COMPANY_CODE_MAP.put("安能物流", "annengwuliu");
    }

    @PostConstruct
    public void init() {
        log.info("快递100物流服务初始化成功, key={}, customer={}", key.substring(0, 4) + "***", customer.substring(0, 6) + "***");
    }

    @Override
    public Map<String, Object> queryTracking(String logisticsCompany, String logisticsNo) {
        Map<String, Object> result = new HashMap<>();
        result.put("company", logisticsCompany);
        result.put("no", logisticsNo);

        String comCode = COMPANY_CODE_MAP.get(logisticsCompany);
        if (comCode == null) {
            result.put("status", "未知快递公司");
            result.put("error", "不支持的快递公司: " + logisticsCompany);
            return result;
        }

        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("com", comCode);
            paramMap.put("num", logisticsNo);
            paramMap.put("resultv2", "4");
            String param = objectMapper.writeValueAsString(paramMap);

            String sign = md5(param + key + customer).toUpperCase();

            String formData = "customer=" + URLEncoder.encode(customer, StandardCharsets.UTF_8)
                    + "&sign=" + URLEncoder.encode(sign, StandardCharsets.UTF_8)
                    + "&param=" + URLEncoder.encode(param, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(queryUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            @SuppressWarnings("unchecked")
            Map<String, Object> apiResult = objectMapper.readValue(response.body(), Map.class);

            if (apiResult.containsKey("result") && Boolean.FALSE.equals(apiResult.get("result"))) {
                result.put("status", "查询失败");
                result.put("error", apiResult.getOrDefault("message", "未知错误"));
                return result;
            }

            String state = String.valueOf(apiResult.getOrDefault("state", "0"));
            result.put("state", state);
            result.put("status", stateToStatus(state));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> data = (List<Map<String, Object>>) apiResult.get("data");
            if (data != null) {
                List<Map<String, String>> traces = new ArrayList<>();
                for (Map<String, Object> item : data) {
                    Map<String, String> trace = new HashMap<>();
                    trace.put("time", String.valueOf(item.getOrDefault("time", "")));
                    trace.put("desc", String.valueOf(item.getOrDefault("context", "")));
                    trace.put("status", String.valueOf(item.getOrDefault("status", "")));
                    trace.put("location", String.valueOf(item.getOrDefault("location", "")));
                    traces.add(trace);
                }
                result.put("traces", traces);
            } else {
                result.put("traces", Collections.emptyList());
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> routeInfo = (Map<String, Object>) apiResult.get("routeInfo");
            if (routeInfo != null) {
                result.put("routeInfo", routeInfo);
            }

        } catch (Exception e) {
            log.error("快递100查询失败: company={}, no={}, error={}", logisticsCompany, logisticsNo, e.getMessage());
            result.put("status", "查询异常");
            result.put("error", e.getMessage());
        }

        return result;
    }

    @Override
    public List<String> getSupportedCompanies() {
        return new ArrayList<>(COMPANY_CODE_MAP.keySet());
    }

    private String stateToStatus(String state) {
        switch (state) {
            case "0": return "运输中";
            case "1": return "已揽收";
            case "2": return "疑难件";
            case "3": return "已签收";
            case "4": return "已退签";
            case "5": return "派件中";
            case "6": return "退回中";
            case "7": return "转投中";
            case "8": return "清关中";
            case "14": return "已拒签";
            default: return "未知状态";
        }
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
}