package com.zhantu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_URLS = {
        "/api/user/login",
        "/api/user/register",
        "/api/user/captcha",
        "/api/user/sendResetCode",
        "/api/user/resetPassword",
        "/api/product/page",
        "/api/product/hot",
        "/api/product/search/hot",
        "/api/product/search/history",
        "/api/product/*",
        "/api/product/category/tree",
        "/api/review/**",
        "/api/admin/login",
        "/api/admin/register",
        "/api/banner/list",
        "/api/upload/**",
        "/api/vehicle/brands",
        "/api/vehicle/series/**",
        "/api/vehicle/models/**",
        "/api/epc/parts",
        "/api/epc/search",
        "/api/service-order/types",
        "/api/coupon/list",
        "/api/stores",
        "/api/membership/**",
        "/api/points-mall/**",
        "/api/promotion/**",
        "/api/logistics/**",
        "/api/payment/notify/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-ui.html",
        "/actuator/health"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/refund/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/purchase-order/**").hasRole("ADMIN")
                .requestMatchers("/api/vehicle/decode", "/api/vehicle/decode-with-parts", "/api/vehicle/vin/**").hasRole("ADMIN")
                .requestMatchers("/api/epc/query-by-vin").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public com.zhantu.filter.JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new com.zhantu.filter.JwtAuthenticationFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
