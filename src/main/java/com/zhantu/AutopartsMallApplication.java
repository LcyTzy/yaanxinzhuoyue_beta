package com.zhantu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutopartsMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutopartsMallApplication.class, args);
        System.out.println("========================================");
        System.out.println("战途汽配商城后端服务启动成功!");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================");
    }
}
