package com.zhantu.interceptor;

import com.zhantu.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        if (uri.contains("/api/user/login") || uri.contains("/api/user/register") ||
            uri.contains("/api/user/captcha") || uri.contains("/api/user/sendResetCode") ||
            uri.contains("/api/user/resetPassword")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未登录，请先登录");
        }

        token = token.substring(7);
        if (jwtUtil.isTokenExpired(token)) {
            throw new RuntimeException("登录已过期，请重新登录");
        }

        Long userId = jwtUtil.getUserId(token);
        request.setAttribute("userId", userId);
        request.setAttribute("role", jwtUtil.getRole(token));
        return true;
    }
}
