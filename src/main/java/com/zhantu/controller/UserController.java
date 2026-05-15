package com.zhantu.controller;

import com.zhantu.annotation.RateLimit;
import com.zhantu.common.BusinessException;
import com.zhantu.common.Result;
import com.zhantu.entity.User;
import com.zhantu.service.CacheService;
import com.zhantu.service.UserService;
import com.zhantu.util.CaptchaUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final CacheService cacheService;

    @GetMapping("/captcha")
    @Operation(summary = "获取图片验证码")
    public Result<Map<String, String>> getCaptcha(HttpSession session) {
        CaptchaUtil.CaptchaResult captcha = CaptchaUtil.generateCaptcha();
        String captchaKey = java.util.UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("captcha:" + captchaKey, captcha.getCode(), 5, java.util.concurrent.TimeUnit.MINUTES);
        session.setAttribute("captcha", captcha.getCode());
        Map<String, String> result = new HashMap<>();
        result.put("image", "data:image/jpeg;base64," + captcha.getImage());
        result.put("captchaKey", captchaKey);
        return Result.success(result);
    }

    @PostMapping("/register")
    @Operation(summary = "注册")
    @RateLimit(time = 60, count = 5)
    public Result<String> register(@RequestBody Map<String, String> registerRequest, HttpSession session) {
        String nickname = registerRequest.get("nickname");
        String phone = registerRequest.get("phone");
        String password = registerRequest.get("password");
        String captcha = registerRequest.get("captcha");
        String captchaKey = registerRequest.get("captchaKey");

        String storedCaptcha = null;
        if (captchaKey != null && !captchaKey.isEmpty()) {
            storedCaptcha = redisTemplate.opsForValue().get("captcha:" + captchaKey);
            redisTemplate.delete("captcha:" + captchaKey);
        }
        if (storedCaptcha == null || storedCaptcha.isEmpty()) {
            storedCaptcha = (String) session.getAttribute("captcha");
            session.removeAttribute("captcha");
        }

        if (storedCaptcha == null || !storedCaptcha.equalsIgnoreCase(captcha)) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        String token = userService.register(nickname, phone, password);
        return Result.success(token);
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    @RateLimit(time = 60, count = 10)
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String phone = loginRequest.get("phone");
        String password = loginRequest.get("password");
        Map<String, Object> result = userService.login(phone, password);
        return Result.success(result);
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getCurrentUser(userId);
        return Result.success(user);
    }

    @PostMapping("/sendResetCode")
    @Operation(summary = "发送重置密码验证码")
    @RateLimit(time = 60, count = 3)
    public Result<String> sendResetCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        if (phone == null || phone.isEmpty()) {
            throw new BusinessException(400, "手机号不能为空");
        }
        userService.sendResetCode(phone);
        return Result.success("验证码已发送");
    }

    @PostMapping("/resetPassword")
    @Operation(summary = "重置密码")
    public Result<String> resetPassword(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");
        String newPassword = request.get("newPassword");
        if (phone == null || code == null || newPassword == null) {
            throw new BusinessException(400, "参数不完整");
        }
        userService.resetPassword(phone, code, newPassword);
        return Result.success("密码重置成功");
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            cacheService.invalidateToken(token);
        }
        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) {
            cacheService.invalidateAllUserTokens(userId);
        }
        return Result.success("退出成功");
    }
}
